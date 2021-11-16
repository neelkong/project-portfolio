# Gitlet Design Document

**Name**: Neel Choudhary

## Classes and Data Structures

### Class 1

#### Fields

1. Field 1
2. Field 2


### Class 2

#### Fields

1. Field 1
2. Field 2

### Commit

#### Instance Variables
* Message - contains the message of a commit.
* Timestamp - time at which a commit was created. Assigned by the constructor.
* Parent - the parent commit of a commit object.

### Repository

#### Instance Variables
* Master - Hash of the head (most current) commit
* Initialized - Says whether the repository has been initialized yet
* SA_files - HashMap that keeps track of file name - file hash code pairings to keep track of which file names each
hash code corresponds to so that the file name can then be looked up when needed.
## Algorithms

## Persistence

