Portal Project Dev environment setup instructions
==================================================
## On MacOS

### Prerequisities
1. Install Homebrew :

   `/usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"`

2. Install Java 8 download : 
NOTE: Brew will download and install latest version of JDK/JRE, its recommended to install JDK/JRE_1.8.0_191 to be in sync with version everyone is using in the team.

Download JDK 1.8-191 from [Java archive downloads](http://www.oracle.com/technetwork/java/javase/downloads/java-archive-javase8-2177648.html), unzip it, then set `JAVA_HOME`, and `PATH` accordingly.

3. Install maven :

   `brew install maven`


4. Install npm (used for front-end):
   `brew install npm`

5. **Set up JAVA_HOME: create or add this to your bash profile `~/.bash_profile` file and add following line:**

```
   ulimit -u 8192
   export JAVA_HOME=$(/usr/libexec/java_home -v1.8)
  
```

6. Go to http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html. Accept the license agreement and download the files. Unzip the files. Copy the two jars to `$JAVA_HOME/jre/lib/security` (you'll probably need to use sudo).

Run this script to test if JCE is installed properly:

`$JAVA_HOME/bin/jrunscript -e 'print (javax.crypto.Cipher.getMaxAllowedKeyLength("RC5") >= 256);'`

If you are under Ubuntu:

```
sudo add-apt-repository ppa:webupd8team/java
sudo apt update
sudo apt install oracle-java8-unlimited-jce-policy
```

7. Update /etc/hosts to reflect your hostname
```
255.255.255.255	broadcasthost
127.0.0.1  <your hostname>
::1        <your hostname>
```

8. Download and install protoc
Complete this step only if you actively working with the protocol buffer files. Maintaining the generated code in the repo allows us to eliminate protoc dependency for developers and project that do not use it.

Go to https://github.com/protocolbuffers/protobuf/releases and find the protoc library for your platform. For macOS its at https://github.com/protocolbuffers/protobuf/releases/download/v3.7.1/protoc-3.7.1-osx-x86_64.zip
Unzip the protoc file and add the bin directory to your path. Below is an example

```
   wget https://github.com/protocolbuffers/protobuf/releases/download/v3.7.1/protoc-3.7.1-osx-x86_64.zip
   unzip protoc-3.7.1-linux-x86_64.zip -d <local_path>
   export PATH=<local_path>/bin:$PATH

```


### Github setup

1. Create harness dedicated github account. Use your harness email.

2. Make your email public as it is shown on the picture:

NOTE: This account will be used mostly in harness private repos, you should not be concerned for being over espoused.

![config image](img/github_email_setup.png)

3. Setup your public profile. The important fields are Name, Public email and Profile picture.
Please enter your First and Last name for the Name and select your harness email as Public email.
Please upload an easy to recognize image preferably of your face - with the team growing the autogenerated frogs from github are hard to use for identification.

NOTE: the data from it is used for every git operation github does on you behave as squashing changes or direct edits. This is why this is important.

![config image](img/github_profile_setup.png)

### Git setup

1. Setup your harness email to the git config. You can do it globally or just for the portal repo:

    `git config --global user.email "email@harness.io"`

    or just for portal with

    `git config user.email "email@harness.io"`

2. Setup your name to the git config. We are using First and Last name. Please make sure you use the same spelling as you did for your github account.

    `git config --global user.name "FirstName LastName"`

    or just for portal with

    `git config user.name "FirstName LastName"`

3. Install git hooks. Portal comes with a set of convenient productivity booster set of hooks. For security reasons they cannot be enabled automatically.
   To do so execute the following command from the root of already cloned locally repo:

    `toolset/git-hooks/install.sh`

    NOTE: if you clone the repo to another location you will have to do this again. On the other side you will be getting fixes and updates with no extra effort.

### Build

1. Clone form git repository: https://github.com/wings-software/portal

   (Optional) Follow https://help.github.com/articles/adding-a-new-ssh-key-to-your-github-account/
   to setup your SSH keys. You can then use SSH to interact with git
   
2. Go to `portal` directory and run

    (Optional) this is needed only if there is a change in the protobuf files
    `mvn -P protobuf clean generate-sources`
    
    `mvn clean install`

3. If Global Search is not required:

    Install and start MongoDB Docker Image (v3.6):
    ```
    $ docker run -p 27017:27017 -v ~/_mongodb_data:/data/db --name mongoContainer -d --rm mongo:3.6
    ```
    Verify the container is running using `docker ps`
    
    Install & use [RoboMongo](https://robomongo.org/download) client to test MongoDB connection.

4. If Global search has to be enabled (OPTIONAL):

    Install and start Elasticsearch Docker Image for Search(v7.3):
    ```
    $ docker run -p 9200:9200 -p 9300:9300 -v ~/_elasticsearch_data:/usr/share/elasticsearch/data -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:7.3.0
    ```
    
    In portal/71-rest/config.yml set `searchEnabled` to `true`. 
    
    Run mongo in replica set:

    ```
    $ docker-compose -f <Directory to portal>/portal/docker-files/mongo-replicaset/docker-compose.yml up -d
    ```

    Add this to /etc/hosts:
    ```
    127.0.0.1       mongo1
    127.0.0.1       mongo2
    127.0.0.1       mongo3
    ```

    Run `brew tap mongodb/brew`
    Run `brew install mongodb-community@4.2`

    Run `mongo --port 30001`

    Run these in the mongo console:
    ```
    rs.initiate()
    rs.add('mongo2:30002')
    rs.add('mongo3:30003')
    ```
    
    In config.yml set `mongo.uri` to `mongodb://mongo1:30001,mongo2:30002,mongo3:30003/harness`.
    Do the same in `config-datagen.yml` and `verification-config.yml`.

5. If TimeScaleDB has to be enabled (Optional for now) 
   
   a. Start TimeScaleDB using the following docker command: `docker run -d --name harness-timescaledb -v ~/timescaledb/data:/var/lib/postgresql/data -p 5432:5432 --rm -e POSTGRES_USER=admin -e POSTGRES_DB=harness -e POSTGRES_PASSWORD=password timescale/timescaledb`
  
   b. Set the TimeScaleDB config in the config.yml
  ``` 
  timescaledb:
    timescaledbUrl: jdbc:postgresql://localhost:5432/harness
    timescaledbUsername: admin
    timescaledbPassword: password
  ``` 
   
### Run Harness without IDE (especially for the UI development)
cd to `portal` directory
1. Start server by running following commands : 

   * `mvn clean install -DskipTests`
   * `java -Xms1024m -Xmx4096m -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:mygclogfilename.gc -XX:+UseParallelGC -XX:MaxGCPauseMillis=500 -Xbootclasspath/p:~/.m2/repository/org/mortbay/jetty/alpn/alpn-boot/8.1.13.v20181017/alpn-boot-8.1.13.v20181017.jar -Dfile.encoding=UTF-8 -jar 71-rest/target/rest-capsule.jar server 71-rest/config.yml > portal.log &`

2. Generate sample data required to run the services locally by running the following step only once.
   DataGenUtil: Open a new terminal and run following command (Make sure you [setup `HARNESS_GENERATION_PASSPHRASE` environment variable](https://docs.google.com/document/d/1CddJtyZ7CvLzHnBIe408tQN-zCeQ7NXTfIdEGilm4bs/edit) in your Bash profile):

   * `java -Xmx1024m -jar 91-model-gen-tool/target/model-gen-tool-capsule.jar 91-model-gen-tool/config-datagen.yml`

3. Start Delegate 

   * `java -Xmx4096m -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:mygclogfilename.gc -XX:+UseParallelGC -XX:MaxGCPauseMillis=500 -jar 81-delegate/target/delegate-capsule.jar 81-delegate/config-delegate.yml &`

4. Start Verification service (Optional) 

   * `java -Xms1024m -Xmx4096m -XX:+HeapDumpOnOutOfMemoryError -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:mygclogfilename.gc -XX:+UseParallelGC -XX:MaxGCPauseMillis=500 -Xbootclasspath/p:~/.m2/repository/org/mortbay/jetty/alpn/alpn-boot/8.1.13.v20181017/alpn-boot-8.1.13.v20181017.jar -Dfile.encoding=UTF-8 -jar 79-verification/target/verification-capsule.jar 79-verification/verification-config.yml > verification.log &`

### Editing setup

1. Install [clang-format](https://clang.llvm.org/docs/ClangFormat.html) ( Install version 7.0.0 by running following 2 [commands](https://gist.github.com/ffeu/0460bb1349fa7e4ab4c459a6192cbb25))
```
curl https://gist.githubusercontent.com/ffeu/0460bb1349fa7e4ab4c459a6192cbb25/raw/4ac5c1aef6d24849b96a0d36b6417798289722fe/clang-format@7.rb -o $(brew --repo)/Library/Taps/homebrew/homebrew-core/Formula/clang-format@7.rb

brew install clang-format@7
```

helper shell scripts:

* `git clang-format` - makes sure all staged in git files are reformatted

* `find . -iname *.java | xargs clang-format -i` - formats all java files from the current directory down

### IntelliJ Setup

1. Install IntelliJ community edition
2. Import `portal` as maven project
3. Install ClangFormatIJ Plugin: https://plugins.jetbrains.com/plugin/8396-clangformatij
   (use `Ctrl/Cmd-Alt-K` to format current statement or the selection)

   **WARNING:** For unclear reason in some environments the plugin causes IntelliJ to hang. If you are unlucky
   to be one of those cases there is alternative. Please use the external 3rd-party tool integration as
   described here: https://www.jetbrains.com/help/idea/configuring-third-party-tools.html.
   Configure the tool to look like shown on the image:

   ![config image](img/clang-format-config.png).

   Then follow these instructions https://www.jetbrains.com/help/idea/configuring-keyboard-shortcuts.html to
   assign whatever key combination you would like it to be triggered on.

4. Install Lombok Plugin: https://projectlombok.org/setup/intellij
5. Install SonarLint plugin:
   - This plugin is really helpful to analyze your code for issues as you code.
   - Go to `Preferences -> Plugins` ->  type SonarLint -> Install plugin. (Will need to restart Intellij)
   - Go to `Preferences -> Other settings -> Sonarlint general settings`. Check "Automatically trigger analysis". Add a connection to `https://sonar.harness.io`. You'll need to create a custom token.
   - Go to `Preferences -> Other settings -> Sonarlint project settings`. Check "Bind project to sonarqube", and select the connection, and set project as `portal`. This is so that we use the same rules locally instead of the default rules.
    ![config image](img/sonar-config.png).
   - Go to `Preferences -> Editor -> Colorscheme -> Sonarlint`. For Blocker, Critical & Major, untick "Inherit values from" checkbox and configure a different highlighting style. These violations are treated as release blockers and this configuration is to highlight them differently from regular warnings.
    ![config image](img/sonar-highlight-config.png).
   - Just right click on file in intellij and "Analyze with SonarLint" or enable autoscan.
6. Install the [IntelliJ Checkstyle Plugin](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea). Setup Checkstyle plugin. In `Preferences -> Other settings -> Checkstyle` add `tools/config/target/config-0.0.1-SNAPSHOT-jar-with-dependencies.jar` and `tools/checkstyle/target/checkstyle-0.0.1-SNAPSHOT.jar` jars in the repo to the 3rd party checks classpath. Add configuration file `harness-checks.xml` (Choose the option to resolve the file from the 3rd party checks classpath - it's within the config jar) and choose it as the default active. Set scan scope to  `java sources including tests`.
   *  ![config image](img/checkstyle-config-pre.png).
   *  ![config image](img/checkstyle-config.png).
7. Change settings to mark injected fields as assigned. (Settings > Editor > Inspections > Java > Declaration Redundancy > Unused Declarations>Entry Points >
   Annotations > Mark field as implicitly written if annotated by) Click add, then search for "Inject". Add both google and javax annotations.
   *  ![config image](img/annotation_config.png).

8. Increase Build Process Heap Size (Preferences > Build, Execution, Development > Compiler, search for "Build Process Heap Size" and set it to 2048 or higher if you still see an out of memory exception in future)


### Run from IntelliJ

Run configurations for the different applications are already checked into the repo. Choose the appropriate run configuration from the menu.
![Run configuration menu](img/run_configs.png) 


### Show current git branch in command prompt

Add the following to your `~/.bash_profile` to display the current git branch in the command prompt:

```
parse_git_branch() {
  git branch 2 > / dev / null | sed - e '/^[^*]/d' - e 's/* \(.*\)/ (\1)/'
}
export PS1="\[\033[34m\]\w\[\033[36m\]\$(parse_git_branch)\[\033[31m\] $\[\033[0m\] "
```

Alternatively, use Fish shell: `brew install fish` then set iterms command to `/usr/local/bin/fish`

### Before you can use the client:

1. Make sure your mongodb is running first.

2. Run API Server (WingsApplication): [Run > Run... > WingsApplication]

3. From within the IDE, run `rest/src/test/java/software/wings/integration/DataGenUtil.java` and

4. `rest/src/test/java/software/wings/service/impl/RoleRefreshUtil.java` to create the default users and roles.

5. Run DelegateApplication: [Run > Run... > DelegateApplication]  

The admin username and password are in BaseIntegrationTest.java.

### Note:

1. To build UI Go to wingsui and follow READ me instructions.

2. To apply database migrations run following command in dbmigrations folder:

   `mvn clean compile exec:java`

### Common problems:
* If you get an error about missing build.properties when you start the server, do a mvn clean install.
* If you get a SupportedEllipticCurvesExtension NoClassDefFoundError, Its likely that jsse.jar in /Library/Java/JavaVirtualMachines/<JDK Version>/Contents/Home/jre/lib folder does not have this class definition. Copy this file from a Team member.
    * If you have `jsse.jar` but still getting that error, then make sure the default JDK for your maven module is set correctly in IntelliJ. Right Click Module in left sidebar > Open Module Settings > Platform Settings > SDKs)
* If you go to https://localhost:8000/#/login and don't see content, go to https://localhost:8181/#/login to enable the certificate then try again.
* If still face not able to login then got to https://localhost:9090/api/version and enable certificate and try again.

### Python

* Refer to the readme under python/splunk_intelligence

### Troubleshooting

https://github.com/wings-software/portal/wiki/Troubleshooting-running-java-process
