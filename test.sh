#!/usr/bin/env bash
# Bien dich va chay bo test (khong anh huong data/club.db).
cd "$(dirname "$0")" || exit 1
rm -rf out-test && mkdir -p out-test
javac -encoding UTF-8 -cp "lib/*" -d out-test $(find src test -name "*.java") || exit 1
java -Dfile.encoding=UTF-8 -cp "out-test:lib/*" clubmanagement.TestRunner
status=$?
rm -rf out-test
exit $status
