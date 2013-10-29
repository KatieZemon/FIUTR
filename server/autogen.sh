#!/bin/sh -e

# Taken from systemd autogen.sh. No committing whitespace errors.
if [ -f .git/hooks/pre-commit.sample ] && [ ! -f .git/hooks/pre-commit ]; then
        # This part is allowed to fail
        cp -p .git/hooks/pre-commit.sample .git/hooks/pre-commit && \
        chmod +x .git/hooks/pre-commit && \
        echo "Activated pre-commit hook." || :
fi

autoreconf --force --install --verbose

echo
echo "========================================================================"
echo "Now you can call ./configure. For a good development configuration, try"
echo
echo "./configure CXXFLAGS='-Wall -Wextra -pedantic -O0 -g'"
echo "========================================================================"
echo
