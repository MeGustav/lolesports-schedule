#!/bin/sh

# Define steps needed to adequately start the application
unzip dist/target/distributive.zip -d HerokuDeploy
cd HerokuDeploy/distributive/bin
sh ./startup.sh