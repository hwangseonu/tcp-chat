from socket import *
from threading import Thread, Lock

HOST, PORT = '0.0.0.0', 7007
BUF_SIZE = 1024
lock = Lock()
socket_dict = {}


def handle_socket(sock: socket, address):
    name = sock.recv(BUF_SIZE).strip().decode()
    name += f"({address[0]}:{address[1]})"
    broadcast(f"{name} enter this chat.")

    with lock:
        socket_dict[name] = sock

    while True:
        msg = sock.recv(BUF_SIZE).strip().decode()
        broadcast(f"{name}: {msg}")


def broadcast(msg):
    print(msg)

    with lock:
        msg += "\n"
        for sock in socket_dict.values():
            sock.sendall(msg.encode())


if __name__ == '__main__':
    server = socket(AF_INET, SOCK_STREAM)
    server.bind((HOST, PORT))

    server.listen()

    while True:
        client, addr = server.accept()
        th = Thread(target=handle_socket, args=(client, addr))
        th.start()




