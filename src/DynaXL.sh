#!/bin/bash
if command -v java >/dev/null ; then
    echo found java executable in PATH
    _java=java
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
    echo found java executable in JAVA_HOME     
    _java="$JAVA_HOME/bin/java"
else
    echo "no java"
    exit 2
fi

#if [[ "$_java" ]]; then
if test "$_java"- ; then
    version=$("$_java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo version "$version"
    #if [[ "$version" > "1.8" ]]; then
    if [ "$version" > "1.8" ]; then
        echo version is more than 1.8
    else         
        echo version is less than 1.8, DynaXL need java version bigger than 1.8
        exit 3
    fi
fi
if command -v pushd >/dev/null ;then 
    pushd `dirname $0` > /dev/null
    SCRIPTPATH=`pwd -P`
    popd > /dev/null
elif command -v readlink >/dev/null ;then
    # Absolute path to this script. /home/user/bin/foo.sh
    SCRIPT=$(readlink -f $0)
    # Absolute path this script is in. /home/user/bin
    SCRIPTPATH=`dirname $SCRIPT`
else
    echo DynaXL need bash and a POSIX system
    exit 3
fi
echo $SCRIPTPATH
java -jar $SCRIPTPATH/DynaXL.jar &
