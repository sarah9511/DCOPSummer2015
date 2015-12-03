

import sys
import os
import subprocess as s

proc = s.Popen("ssh rstanton@euler.cs.nmsu.edu")
proc.stdin.write("Pikchu1")