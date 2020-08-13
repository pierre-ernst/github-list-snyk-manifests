# github-list-snyk-manifests
Create a CSV list of manifest files (supported by Snyk) from a GitHub org or repo

## Usage
```
$ ./gradlew run octocat Hello-World > list.csv
$ cat list.csv
"org", "repo", "manifest"
"octocat", "Hello-World", "build.gradle.kts"
"octocat", "Hello-World", "foo/requirements.txt"
```


