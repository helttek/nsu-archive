import argparse

from proxy import *
from proxy_logger.default_proxy_logger import DefaultProxyLogger

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('-p', '--port', type=int, help='proxy port', required=True)
    args = parser.parse_args()
    port = args.port
    logger = DefaultProxyLogger()
    proxy = Proxy(port=port, logger=logger)
    proxy.launch()
