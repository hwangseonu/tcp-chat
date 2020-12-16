from socket import *
from threading import Thread
import argparse


PORT = 7007
BUF_SIZE = 1024


def print_thread(sock: socket):
    while True:
        recv = sock.recv(BUF_SIZE).strip().decode()
        print("\r" + recv)
        print("> ", end="")


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('server')
    args = parser.parse_args()

    sock = socket(AF_INET, SOCK_STREAM)
    sock.connect((args.server, PORT))

    name = input("Username: ") + "\n"
    sock.send(name.encode())

    th = Thread(target=print_thread, args=(sock,))
    th.start()

    while True:
        print("> ", end="")
        line = input() + "\n"
        sock.send(line.encode())

