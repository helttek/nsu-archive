import os
import socket
import ipaddress
import struct


class self_copy_detector:
    __DEFAULT_PORT = 8888
    __DEFAULT_IP_ADDRESS = '224.0.0.1'  # FF02::1
    __TTL = 1
    __connections = dict()
    __BUF_SIZE = 128
    __update_flag = False

    def __init__(self, argv):
        match len(argv):
            case 1:
                self.ip_address = self.__DEFAULT_IP_ADDRESS
                self.port = self.__DEFAULT_PORT
            case 2:
                self.ip_address = argv[1]
                self.port = self.__DEFAULT_PORT
            case 3:
                self.ip_address = argv[1]
                self.port = argv[2]
            case _:
                raise RuntimeError(
                    f'Expected 2 arguments - [IP address, port], but got: {argv[1:]}')
        self.__create_socket()

    def __check_addr_and_port_validity(self):
        try:
            ipaddress.ip_address(self.ip_address)
        except ValueError:
            raise RuntimeError(
                f'Error: [{self.ip_address}] is not a valid IP address')
        try:
            port = int(self.port)
            if port < 0 or port > 65535:
                raise RuntimeError(f'Error: [{self.port}] is not a valid port')
        except ValueError:
            raise RuntimeError(f'Error: [{self.port}] is not a valid port')

    def __create_socket(self):
        self.__check_addr_and_port_validity()

        # sending
        self.addrinfo = socket.getaddrinfo(self.ip_address, None)[0]
        self.sock = socket.socket(self.addrinfo[0], socket.SOCK_DGRAM)
        ttl_bin = struct.pack('@i', self.__TTL)
        if self.addrinfo[0] == socket.AF_INET:
            self.sock.setsockopt(
                socket.IPPROTO_IP, socket.IP_MULTICAST_TTL, ttl_bin)
        else:
            self.sock.setsockopt(socket.IPPROTO_IPV6,
                                 socket.IPV6_MULTICAST_HOPS, self.__TTL)

        # receiving
        self.sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.sock.bind(('', self.port))
        ip_address_bin = socket.inet_pton(
            self.addrinfo[0], self.addrinfo[4][0])
        if self.addrinfo[0] == socket.AF_INET:
            mreq = ip_address_bin + struct.pack('=I', socket.INADDR_ANY)
            self.sock.setsockopt(
                socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)
        else:
            mreq = ip_address_bin + struct.pack('@I', 0)
            self.sock.setsockopt(socket.IPPROTO_IPV6,
                                 socket.IPV6_JOIN_GROUP, mreq)

    def __print_active_connections(self):
        print("-------------------------------------------------------")
        print("Active connections: ")
        i = 1
        for addr in self.__connections:
            if self.__connections[addr] != "":
                print(f"{i}. [{addr}, {self.__connections[addr]}]")
            else:
                print(f"{i}. [{addr}]")
            i += 1

    def __get_key(self, ip_address, data):
        try:
            if int(data[0]) <= 0:
                raise RuntimeError(
                    f'Error: [{int(data[0])}] is not a valid pid')
        except ValueError:
            raise RuntimeError(
                f'Error: [{data[0]}] is not a number (pid should be a positive integer)')
        # print(str(ip_address))
        return str(ip_address) + "; " + str(data[0])

    def __get_value(self, data):
        if data.__len__() > 2:
            print(
                "more than 2 logical data buffers received, skipping each except the first one")
        if data.__len__() == 2:
            return data[1]
        else:
            return ""

    def __add_connection(self, str_data, sender_addr):
        parsed_data = str_data.split(',')
        key = self.__get_key(sender_addr, parsed_data)
        value = self.__get_value(parsed_data)
        if key in self.__connections:
            if value == 'FINISHED':
                self.__update_flag = True
                del self.__connections[key]
        else:
            self.__connections[key] = value
            self.__update_flag = True
            self.sock.sendto((str(os.getpid())).encode(
            ), (self.addrinfo[4][0], self.port))

    def start(self):
        try:
            print("Waiting for connection...")
            print("my pid: " + str(os.getpid()))
            self.sock.sendto((str(os.getpid())).encode(),
                             (self.addrinfo[4][0], self.port))
            while True:
                data, sender_addr = self.sock.recvfrom(self.__BUF_SIZE)
                try:
                    str_data = data.decode()
                    self.__add_connection(str_data, sender_addr)
                except UnicodeDecodeError:
                    print(f'failed to decode a message from {
                          sender_addr}, skipping this message')
                if self.__update_flag:
                    self.__print_active_connections()
                self.__update_flag = False
        except KeyboardInterrupt:
            self.sock.sendto(
                (str(os.getpid()) + ",FINISHED").encode(), (self.addrinfo[4][0], self.port))
            self.sock.close()
            return
