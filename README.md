# BaseProjectMvvm
## Step 1. Add the JitPack repository to your build file
```
allprojects {
   repositories {
      ...
      maven { url 'https://jitpack.io' }
      }
   }
```
## Step 2.在app的build.gradle里配置
```
//在dependencies内部引入库

  dependencies {
  
     implementation 'com.github.szhdev:BaseProjectMvvm:v1.0.2'
     
 }
```
