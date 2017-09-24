import os
import sys
import zipfile
from shutil import rmtree
from subprocess import call

"""
Replacing placeholder properties with the real ones
"""
def replaceprops(file):
    with open(file,'r') as f:
        newlines = []
        for line in f.readlines():
            newlines.append(line
                            .replace('<TELEGRAM_BOT_NAME>', os.environ['BOT_NAME'])
                            .replace('<TELEGRAM_BOT_TOKEN>', os.environ['BOT_TOKEN'])
            )
        with open(file, 'w') as f:
            for line in newlines:
                f.write(line)


print 'Unpacking new distributive...'
with zipfile.ZipFile('/app/dist/target/distributive.zip') as file:
    file.extractall('./HerokuDeploy')
print 'Unpacking finished'

dist = '/app/HerokuDeploy/distributive'

print 'Replacing properties placeholders with real parameters...'
replaceprops(dist + '/conf/application.properties')
print 'Properties replaced'

print 'Starting application...'
os.chdir(dist + '/bin')
call(['sh', 'startup.sh'])