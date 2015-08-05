# Europeana.eu for Android

This is a demo application for the Europeana.eu API version 2.

## Contributing to this project
As this is an Open Source project, all contributions/suggestions are welcome.
You can contribute in several ways.

### Report bugs/suggestions
Use the Issues functionality on 
[our GitHub page](https://github.com/eLedge/europeana4android) to add bug reports 
or issues. Try to be as specific as possible, mention brand and type of 
hardware, android version, screen size etc etc.

### Translating
Help us translating this app via our
[online translations tool](https://eledge.oneskyapp.com/collaboration) so we can
offer the app in more languages!

### Contribute code
* __Get a fork__: The easiest way is to create a fork of our project, make 
your changes and submit a pull request. Some of the core developers will 
look at your request and hopefully merge it into the master branch.

* __Join the team__: It's also possible to be added to the development team 
and submit directly into the repository. But it would be nice if we know you 
a bit, so use the fork method first a few times. :-) 

### Contribute translations
If you want to contribute or correct translations of the application there
are two ways to do so. We try to find a better way of doing this but till 
then...

1. __Pull requests__: same as the code contribution method, fork the project
and add the language folder and the translated `strings.xml` or update an 
existing one. Commit and send us a pull request to merge the translations 
into our master branch.
2. __Submit as issue__: download this file: 
[strings.xml](https://raw.github.com/eLedge/europeana4android/blob/master/res/values/strings.xml)
and translate it. Create an issue starting with the language and it's code 
and post the complete translation into the issue between three of markdown 
code quotes. A preview should look like this:

  ```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
  <string name="app_name">Europeana.eu</string>
</resources>
```

## Setup project
This project is using the gradle building system coming with Android Studio.
Setting up the project is relatively easy.

### Development Environment
This project requires the following setup:
* __Android Studio 1.3.0__ or higher
* __JDK 1.7__ or higher
* __Gradle 2.4__ or higher

Check **build.gradle** which SDK and build tools are required via the SDK manager.

### Creating a project
1. Check out europeana4android on GIT.
2. In the europeana4android project, copy the src/main/AndroidManifest.xml.template
to AndroidManifest.xml in the same folder. The copied file is configured to be
ignored by GIT.
3. Import the project folder into AndroidStudio using the import project function.
If you want to use gradle wrapper or a local gradle installation is up to you.
4. Before testing you are required to have a Google Maps API key and combination
of Europeana API public and private key.

## Project planning

We have our to do list on a trello scrum board (although we don't do sprints (yet))

<https://trello.com/b/AykTks3F/europeana-4-android>

## More project information

For more detailed project documentation, visit the WIKI pages on:

<https://github.com/eLedge/europeana4android/wiki>
