# bigaar

A Gradle plugin to shade dependencies into `.aar` files.

## Installation

### Using the `plugins {}` block

Add the `pink.madis.bigaar` plugin to your plugins block in the library project
you want to shade:

```groovy
plugins {
  id 'pink.madis.bigaar' version '0.1.1'
}
```

### Using the `buildscript {}` block

In your root `build.gradle` file make sure `mavenCentral` is in your
repositories and add the `pink.madis.bigaar:gradle-plugin` to your buildscript
classpath:

```groovy
buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath 'pink.madis.bigaar:gradle-plugin:0.1.1'
  }
}
```

Apply the plugin in your library project:

```groovy
apply plugin: 'pink.madis.bigaar'
```

## Usage

You can configure the plugin using the `bigaar` extension:

```groovy
bigaar {
  // required: repackage shaded classes under this prefix, e.g.
  // okio.Sink would become pink.madis.internal.okio.Sink when shaded
  repackagePrefix = "pink.madis.internal"

  // optional: use ignorePrefixes to pass some classes through unshaded
  ignorePrefixes = ["pink.madis"]
}
```

## Roadmap

Contributions welcome!

- [x] basic shading of .jar files
- [x] support for shading in-module projects
  - **CAVEAT**: currently only works for single-variant in-module projects,
    like release or debug. See [lib-other build.gradle file](sample/lib-other/build.gradle) as an example
- [ ] handling for shading partial dependency trees (i.e. a shaded lib
  transitively depends on a non-shaded library)
- [ ] automatically add proguard consumer rules from input libraries
- [ ] support for shading in `.aar` libraries
- [ ] a few integration tests wouldn't hurt

## License

Standard MIT, see the [license file](LICENSE).
