#!/bin/bash

##
## PostgreSQL Database Backup Script
## Copyright 2010 Wyona
##
## Note: If you want this script to work, PostgreSQL must
## be configured to trust the local machine! This should
## be the default, otherwise refer to the documentation.
##

##
## Settings
##

# Where to store the backups
# Should probably be a full path
BACKUP_DIRECTORY=./backups

# Paths/programs
PG_DUMP=/usr/bin/pg_dump

# Number of backups to keep
MAX_BACKUPS=10

# How the files should be called
# Rotation numer will pre prepended
# Tablename, timestamp will be appended
# You shouldn't have to change this
FILENAME=backup

##
## Code
##

# Get username and table from args
DB_USER=$1
DB_TABLE=$2

if [ -z "$DB_USER" ] || [ -z "$DB_TABLE" ]; then
    echo "Usage: $0 username table"
    exit 7
fi

# Does backup dir exist?
if ! [ -e $BACKUP_DIRECTORY ]; then
    echo "Unable to back up database!"
    echo "Backup directory does not exist: $BACKUP_DIRECTORY"
    exit 1
fi

# Is backup dir actually a directory?
if ! [ -d $BACKUP_DIRECTORY ]; then
    echo "Unable to back up database!"
    echo "Backup directory is not a directory: $BACKUP_DIRECTORY"
    exit 2
fi

# Does pg_dump exist?
if ! [ -e $PG_DUMP ]; then
    echo "Unable to back up database!"
    echo "Can't find the pg_dump binary: $PG_DUMP"
    exit 3
fi

# Is pg_dump executable?
if ! [ -e $PG_DUMP ]; then
    echo "Unable to back up database!"
    echo "The pg_dump binary is not executable: $PG_DUMP"
    exit 4
fi

# Go to backup dir
cd $BACKUP_DIRECTORY

# Dump database
echo -n "Dumping database... "

DUMP=$(mktemp)
$PG_DUMP -U $DB_USER $DB_TABLE > $DUMP

if [ $? -ne 0 ]; then
    echo 
    echo "Unable to back up database!"
    echo "The pg_dump program failed to run properly."
    exit 5
else
    echo "done."
fi

# Rotate backup files
echo -n "Rotating files... "

PREV_NUM=10
for NUM in $(seq -w $MAX_BACKUPS -1 1); do
    FILE=$(find -name "$NUM-$DB_TABLE-$FILENAME-*.sql")
    if [ -f "$FILE" ]; then
        # Last file? If so, delete, else move
        if [ $NUM -eq $MAX_BACKUPS ]; then
            rm $FILE
        else 
            rename $NUM $PREV_NUM $FILE
        fi
    fi
    PREV_NUM=$NUM
done

echo "done."

# Move database dump
echo -n "Moving database dump... "

TIMESTAMP=$(date +'%Y-%m-%d-%H:%M:%S')
mv $DUMP 01-$DB_TABLE-$FILENAME-$TIMESTAMP.sql

if [ $? -ne 0 ]; then
    echo "Unable to back up database!"
    echo "Can't move database dump from $DUMP to $BACKUP_DIRECTORY."
    exit 6
fi

echo "done."

# Go back to original directory
cd $OLDPWD
