import socket
import threading
import os
import time


def handle_client(conn, client_address):
    BUF_SIZE = 4096
    try:
        file_name_size = int.from_bytes(conn.recv(4))
        file_name = conn.recv(file_name_size).decode('utf-8')
        file_size = int.from_bytes(conn.recv(8))
        upload_dir = 'uploads'
        os.makedirs(upload_dir, exist_ok=True)
        file_path = os.path.join(upload_dir, os.path.basename(file_name))
        received_size = 0
        start_time = time.time()
        last_report_time = start_time
        with open(file_path, 'wb') as file:
            while received_size < file_size:
                buf = conn.recv(BUF_SIZE)
                if not buf:
                    break
                file.write(buf)
                received_size += len(buf)
                current_time = time.time()
                if current_time - last_report_time >= 3 or received_size == file_size:
                    elapsed_time = current_time - start_time
                    instant_speed = len(
                        buf) / (current_time - last_report_time)
                    average_speed = received_size / elapsed_time
                    print(f"{client_address} instant speed: {
                          instant_speed} B/s, average speed: {average_speed} B/s")
                    last_report_time = current_time
        if received_size == file_size:
            conn.sendall(b'SUCCESS')
            print(f"{client_address} File received successfully: {file_path}")
        else:
            conn.sendall(b'FAILURE')
            print(f"{client_address} File transfer failed")
    except Exception as e:
        print(f"{client_address} Error: {e}")
    finally:
        conn.close()


def start_server(port):
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.bind(('0.0.0.0', port))
    sock.listen()
    print(f"Listening on port {port}")
    try:
        while True:
            conn, client_address = sock.accept()
            print(f"Established connection with {client_address}")
            thread = threading.Thread(
                target=handle_client, args=(conn, client_address))
            thread.setDaemon = True  # set detached
            thread.start()
    except KeyboardInterrupt:
        print("Shutting down server")
        sock.close()


if __name__ == "__main__":
    import sys
    if len(sys.argv) != 2:
        print("ERROR: no port provided")
    else:
        start_server(int(sys.argv[1]))
