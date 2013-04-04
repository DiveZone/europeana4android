# Europeana.eu for Android

This is a demo application for the Europeana.eu API version 2.

## Contributing to this project
As this is an Open Source project, all contributions/suggestions are welcome.
You can contribute in several ways.

### Report bugs/suggestions
Use the Issues functionality on 
[our GitHub page](https://github.com/eLedge/europeana.eu) to add bug reports 
or issues. Try to be as specific as possible, mention brand and type of 
hardware, android version, screen size etc etc.

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
2. __Submit as issue__: download the 
[`strings.xml`](https://raw.github.com/eLedge/europeana.eu/blob/master/res/values/strings.xml)
and translate it. Create an issue starting with the language and it's code and 
post the complete translation into the issue between three of this quote: `. 
A preview should look like this:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
  <string name="app_name">Europeana.eu</string>
</resources>
```

## Setup project
... TODO ...

### Needed libraries
The project is using two free available Android libraries which you need to 
setup first.

* __Google Play Services__ revision 5 or higher
* __eLedge Android Toolkit__ (open source, master version)

#### Setting up Google Play Services

Google Play Services are downloadable through the 
`Android SDK Manager`. Start the manager and select `Google Play services` on
the `Extras` tree item.

... TODO ...

#### Setting up eLedge Android Toolkit
At the moment the project depends on the master branch of eLAT but we should 
work to a tagged version, to prevent compile errors when the library changes.

<https://github.com/eLedge/AndroidToolkit>

... TODO ...

## More project information

For more detailed project documentation, visit the WIKI pages on:

<https://github.com/eLedge/europeana.eu/wiki>
