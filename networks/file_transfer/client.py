import socket
import os
import sys


def send_file(file_path, server_host, server_port):
    BUF_SIZE = 4096
    RESP_SIZE = 128
    try:
        file_size = os.path.getsize(file_path)
        file_name = os.path.basename(file_path)
        with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as client_socket:
            client_socket.connect((server_host, server_port))
            client_socket.sendall(
                len(file_name.encode('utf-8')).to_bytes(4))
            client_socket.sendall(file_name.encode('utf-8'))
            client_socket.sendall(file_size.to_bytes(8))
            with open(file_path, 'rb') as file:
                while (buf := file.read(BUF_SIZE)):
                    client_socket.sendall(buf)
            server_response = client_socket.recv(RESP_SIZE)
            if server_response == b'SUCCESS':
                print("File sent successfully!")
            else:
                print("File transfer failed!")
    except FileNotFoundError as e:
        print(f"Unable to find file: {e}")
    except OSError as e:
        print(f'OS error occured: {e}')
    except TypeError as e:
        print(f'Bad file name: {e}')
    except ValueError as e:
        print(f'Invalid value: {e}')
    except socket.error as e:
        print(f'Socket error occured: {e}')


if __name__ == "__main__":
    if len(sys.argv) != 4:
        print(
            "ERROR: provided arguments are not correct. Should be: [file path, server address, server port]")
    else:
        send_file(sys.argv[1], sys.argv[2], int(sys.argv[3]))
