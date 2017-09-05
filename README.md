# Europeana.eu for Android

This is a demo application for the Europeana.eu API version 2.

## Contributing to this project
As this is an Open Source project, all contributions/suggestions are welcome.
You can contribute in several ways.

### Report bugs/suggestions
Use the Issues functionality on 
[our GitHub page](https://github.com/DiveZone/europeana4android) to add bug reports 
or issues. Try to be as specific as possible, mention brand and type of 
hardware, android version, screen size etc etc.
Valid bug reports will be moved over to our YouTrack ticket system, see link below.

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
a bit, so use the fork method first. :-)

## Setup project
This project is using the gradle building system coming with Android Studio.
Setting up the project is relatively easy.

### Development Environment
This project requires the following setup:
* __Android Studio 1.5.0__ or higher
* __JDK 1.7__ or higher

Check **build.gradle** which SDK and build tools are required via the SDK manager.

### Creating a project
1. Check out europeana4android on GIT.
2. Add to ~/.gradle/gradle.properties the following properties:
  * E4A_API_PUBLIC_KEY (your europeana API public key)
  * E4A_API_PRIVATE_KEY (your europeana API private key)
  * E4A_MAPS_API_KEY (your google maps developer API key)
3. Import the project folder into AndroidStudio using the import project function.
The project is standard configured with a graddle wrapper, but you can also use your local gradle installation.

## Project planning

We have our to do list on a YouTrack scrum board (although we don't do sprints)

<https://eledge.myjetbrains.com/youtrack/issues/E4A>

## More project information

For more detailed project documentation, visit the WIKI pages on:

<https://github.com/DiveZone/europeana4android/wiki>
