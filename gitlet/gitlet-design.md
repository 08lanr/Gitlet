# Gitlet Design Document

**Name**: Ruobin Lan

## Classes and Data Structures
Main: Execute gitlet, commands include init, add, commit, log,find, and more.

Blob: Assigns serial numbers to each blob present in tree

Commit: Implements timestamp and also builds a data strcuture to hold blobs

## Algorithms
Staging a file:
1) check if any files have been made or changed in folder. If there are, mark these files as untracked
2) git add should move a file into a staging area. If file already exists, it should overwrite it. If file is the same, git add should error
3) Assign keys to each blob, makes it easy to add blobs(files) in order to commit 

Committing a file:
1) Copies previous commit to a new one
2) Update content of repository, updating same files and adding new ones
3) Track date and serial number of each commit
4) Track Commits using Binary Trees

Log
1) Display information about each commit, starting from the most recent one
2) Shows commit ID, commit message, and date
3) Possibly use a data structure like HashTable to store commits for easy access

Status
1) Displays all branches, staged files, unstaged files, and modifications
2) Must use some sort of text editing to display 
3) Create a mainstage that stores all the branches. Then, call mainstage[i] to get the status of each of the branches

Checkout
1) Moves "Head" pointer to a different commit, restores repository to files present in that commit
2) Uses Commit Id and Commit name to find previous commit
3) By keeping commits in a tree, we can easily move "up" in order to find the commit ID


## Persistence

We will need to save each commit in a directory in order to easily gain access to them. Being able to access different commits what Git is all about.
In addition, we will need to clearly seperate branches from one another. I plan to this
through storing branches as individual trees in order to properly seperate them and also make them
easier to merge in the future. 

