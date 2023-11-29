# How to use Git:

### Get the initial clone of the repository from github

git clone https://github.com/kibnakamoto/GrocerList

### To list the possible changes that you might want to do

```
git status
```

### Add changes to commit

```
git add <file name(s)>
```

### Commit changes

This is to commit the changes. Add a few word description of what you changed in the file

```
git commit -m "<message>"
```

### Push changes unto Github

```
git push
```

### Pull changes from Github

This is to make sure that everything is up to date.

```
git pull
```

For example, added a comment on a function. type ```git add <filename>```, then ```git commit -m "Added a comment for function-name function"```
If you want to push this change. Type git push.

If you have lots of changes and you don't know if they are valid changes and you don't want to corrupt the original version on gihtub. You can create a branch and push everything there. And then make a pull request from github and others will review and come to a conclusion. This is not necessary in most cases unless you believe that the changes you made might be ruining the original branch.

### To create branch

```
git branch <branch-name>
```

### To list the available branches

```
git branch
```

### Select the branch to work on

```
git checkout <branch-name>
```

### to make the pull request, Just use Github

Please note that using branches are often annoying.
