import os
import sys
import subprocess
import zipfile
from shutil import rmtree

if (len(sys.argv) != 5):
    print('Usage: .py <dist> <targetPath> <botName> <botToken>')
    sys.exit()

dist = sys.argv[1]
targetPath = sys.argv[2]
botName = sys.argv[3]
botToken = sys.argv[4]

"""
Replacing placeholder properties with the real ones
"""
def replaceProps(file):
    with open(file, 'r') as f:
        newlines = []
        for line in f.readlines():
            newlines.append(line
                            .replace('<TELEGRAM_BOT_NAME>', botName)
                            .replace('<TELEGRAM_BOT_TOKEN>', botToken)
            )
        with open(file, 'w') as f:
            for line in newlines:
                f.write(line)


print('App path: ', targetPath)

if (os.path.exists(targetPath)):
    print('Shutting down existing bot...')
    subprocess.call(['sh', targetPath + '/distributive/bin/shutdown.sh'])
    print('Bot was shut down')
    print('Removing old distributive...')
    rmtree(targetPath)
    print('Removal finished')

print('Unpacking new distributive...')
with zipfile.ZipFile(dist) as file:
    file.extractall(targetPath)
    print('Unpacking finished')

os.chdir(targetPath + "/distributive/conf")
replaceProps('./application.properties')