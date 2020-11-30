#!/bin/sh
#
# An example hook script to verify what is about to be committed.
# Called by "git commit" with no arguments.  The hook should
# exit with non-zero status after issuing an appropriate message if
# it wants to stop the commit.
#
# To enable this hook, rename this file to "pre-commit".
 
mvn validate
if [ $? -eq 0   ]; then
    echo "checkstyle OK"
else
    exit 1
fi
