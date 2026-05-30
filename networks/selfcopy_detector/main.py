from self_copy_detector import self_copy_detector
import sys

try:
    scd = self_copy_detector(sys.argv)
    scd.start()
except RuntimeError as e:
    print(e)
    sys.exit(1)
