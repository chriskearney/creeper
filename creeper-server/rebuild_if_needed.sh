#!/bin/sh

cd /home/creeper/creeper

git remote update > /dev/null 2>&1

needsUpdate () {
    UPSTREAM=${1:-'@{u}'}
    LOCAL=$(git rev-parse @)
    REMOTE=$(git rev-parse "$UPSTREAM")
    BASE=$(git merge-base @ "$UPSTREAM")

    if [ $LOCAL = $REMOTE ]; then
        return 1
        #echo "Up-to-date"
    elif [ $LOCAL = $BASE ]; then
        return 0
        #echo "Need to pull"
    #elif [ $REMOTE = $BASE ]; then
        #echo "Need to push"
    #else
        #echo "Diverged"
    fi
    return 1
}

if needsUpdate; then
    git pull ; mvn clean install
else
    echo "No rebuild is needed."
fi
