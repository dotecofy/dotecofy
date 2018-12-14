# dotecofy
Document, test, code and verify. Tool and methodology to develop good quality programs

## Introduction

Dotecofy means DOcument, TEst, COde and veriFY. This is a tool and a methodology to help develop better quality programs.

The aim is not to focus first on the final product delivered to the customers but rather on the documentation and the tests. In fact, they are considered the real product, the code is the result or the consequence of the product.
In this paradigm, the joke that says a bug is an undocumented feature is almost true. The code is not considered as the issue rather, the documentation and the tests have not been able to cover the case in the current version.

This methodology does not say what is a good documentation or what are good tests. This is up to the developer or the team to decide what is satisfying or not.

It is also important to understand that following this methodology does not necessarily guarantee the quality. The only think that does this methodology is to give more credibility to documenting and testing and less to the code.
The assumption behind all of it is that if writing unit tests and documentation is considered as boring and annoying if the focus is given to documenting and testing, then coding becomes boring. However, you will write the code because you want to be paid or gratified by the praises of the community ;)

## The four steps

These four steps are not something new under the sun, if they are even qualified as different it is because the scene is observed from another perspective. 

### Step 1: document

The documentation is one of the keys to qualify a project as good quality. If nobody understands how it works and how to use it, even if the project seems amazing, it cannot be used. A potential customer or user will most likely choose something else.

Starting by writing the documentation forces us to think about what we are going to do and prevent us from rushing into whatever we may do before starting thinking.

If the documentation is good, everything will be clearer for everybody.

The documentation, together with the tests are considered as the product. Therefore, it is completely valid to think that there are different versions with new features and improvement.

### Step 2: test

As soon as a functionality, a task, or an improvement is well documented, the tests are created to explain how a code can be proven to be working or they are created to automatically prove that the code works. 

### Step 3: code

Code or implement. During this phase a code that should comply with the documentation and the tests will be produced.

### Step 4: verify

In step 2 you have described the tests or created automated tests. Now, it is time to apply them and see if the code produced is acceptable. Tests are not necessarily automatized, a test saying to verify manually if a table has been created in the database is perfectly valid.

During this phase, code is verified through the tests but it can also be reviewed by another team.

## Artefacts

### Project

### Component

### Feature

### Version

### Improvement

## Installation

### Creation of the database (MariaDB or MySQL)

CREATE DATABASE dotecofy CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'dotecofy_user1'@'localhost' identified by 'Pa$$1337';
GRANT ALL PRIVILEGES ON dotecofy.* TO 'dotecofy_user1'@'localhost';

Please change the password


