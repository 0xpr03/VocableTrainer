VocableTrainer
==============

Simple vocable trainer in java.  
[Android version](https://github.com/0xpr03/VocableTrainer-Android)

### Features
- create lists as A - B column
- train in an A -> B, B -> A or random mode
- training with vocables not used since X days
- stop training at any time and safe your progress
- training over multiple lists
- runnable from linux/mac/windows
- import/export from/to CSV files

### Technical notes:
- sqlite3 is used for data storage
- java 8 required
- using own CSV notation for metadata export/import

### Missing features
- creating new lists based on selected ones in existing lists
