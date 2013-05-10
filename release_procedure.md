# Release Procedure

Releasing a new version of Woodpile requires the following manual steps performed within Eclipse.

## Build Woodpile

In the order listed, update the version and use the Export Wizard to build the plugin or feature into the project for the update site (com.hudren.update).

### com.hudren.woodpile

1. Edit the `plugin.xml` file
1. Update the version number on the Overview tab
1. Export the plugin to the com.hudren.update project

### com.hudren.woodpile.startup

1. Edit the `plugin.xml` file
1. Update the version number on the Overview tab
1. Export the plugin to the com.hudren.update project
	
### com.hudren.woodpile.feature

1. Edit the `feature.xml` file
1. Update the version number on the Overview tab
1. Export the feature to the com.hudren.update project

## Build the update site

1. Edit the `site.xml` file in the com.hudren.update project
2. Add a new feature to the Development category
3. Select the new feature and click the Build button

## Publish the update site

Copy any new or updated files to the web server hosting the update site.