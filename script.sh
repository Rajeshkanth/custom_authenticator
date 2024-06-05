#!/bin/bash

## This script will create the jar files and move the jar files in specific destination/folder.
## It will reduce the time for moving the files manually after creating the jar file.

## Build jar command
mvn clean package

# Directory to search and the target directory to move the file to
search_dir="./target"
target_dir="../keycloak-24.0.3/providers"
file_name="*.jar"

echo "Searching in directory: $search_dir"
echo "Target directory: $target_dir"
echo "File name pattern: $file_name"

# Find the file and move it to the target directory
found_files=$(find "$search_dir" -type f -name "$file_name")

if [ -z "$found_files" ]
then
  echo "No files found matching the pattern: $file_name"
else
  echo "Files found: $found_files"
  for file in $found_files
   do
    if mv -f "$file" "$target_dir"
     then
      echo "Moved file: $file to $target_dir"
    else
      echo "Failed to move file: $file"
    fi
  done
fi
