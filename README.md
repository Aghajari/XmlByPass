# XmlByPass
 
Get the Highest Android UI performance!

With  **XmlByPass** you won't need to learn any new thing! just use  **XmlByPass** to get the highest performance from your Xml layouts.

When i started developing android applications, i just saw that we develop almost all of the UI with Xml and that made me so unsatisfied cuase it was really really boring. So, i started creating views programmatically then i found it so enjoyable and got a better perfomance for sure. But coding programmatically to create a layout is very time consuming even for simple layouts.

and also as [Karakuri](https://stackoverflow.com/a/35569482) said: 
> Remembering which LayoutParams to use where is great mental gymnastics

But i will tell you how to do this later (If you wanted ofc)

As you may know there are so many challenges to create a layout programmatically, 
for example you need to know about qualifiers (Screen orientation, Screen size, Layout Direction, Night mode and etc.)

Android has already handled all of this with resources! [Read more](https://developer.android.com/guide/topics/resources/providing-resources)
> Almost every app should provide alternative resources to support specific device configurations. For instance, you should include alternative drawable resources for different screen densities and alternative string resources for different languages. At runtime, Android detects the current device configuration and loads the appropriate resources for your app.
> 
> <img src="https://user-images.githubusercontent.com/30867537/166199347-b4e3ec18-a33a-41fa-ac30-b750290c4dd2.png" width=300 title="Image">

Why should we use XML layouts?
- As i mentioned, coding programmatically to create a layout is very time consuming meanwhile one of the goals of xml was to prepare quickly.
- You can easily create a layout, even with a simple drag & drop (thanks to the Android Studio Designer tools)
- Almost all of the resources/tutorials of Android on documents/articles/source-codes are presented by **XML**
- Easy to create layouts for different configurations. (Qualifiers)

How `LayoutInflater` inflates a Layout?

Android pre-compiles every layout but still needs to hold the original xml file, why? cause needs to generate a suitable LayoutParams for container.
So, in step of inflation we always are parsing the XML by using [`XmlPullParser`](http://www.xmlpull.org/), but ofc android has written it's own customized parser with native codes (C) which makes it a little faster. Anyway, We never can ignore the fact that creating instance of views are done by reflection (`LayoutInflater#createView(Context, String, String, AttributeSet)`). And without `ViewBinding`, everytime we want to find a `View` we call `View#findViewById(int)` which needs to iterate all childs (including childs of it's child :D)

As [Andrii Drobiazko](https://medium.com/@c2q9450/performance-comparison-building-android-ui-with-code-anko-vs-xml-layout-cc0abb21c561) wrotes: 

> By default, UI in Android is built using XML layout files. it leads to overhead in cpu and ram usage. It can be insensibly for fast and powerful devices, but low-end devices may suffer from resources deficit
> 
> **Benchmark:**
> 
> <img src="https://user-images.githubusercontent.com/30867537/166209006-636b3995-51b7-4741-aa4a-62a22ebd5921.png" width=500 title="Image">

---
<p align="center"><b>And there we go! Your savior, XmlByPass :)</b></p>

---

 ## Table of Contents  
- [Introduction](#introduction)
- [Installation](#installation)
- [Usage](#usage)
- [Author](#author)
- [License](#license)
 
 ## Introduction
 
**XmlByPass** is an annotationProcessor library for Android which auto generates the java code of your xml layouts in `Source` level (before compile). That means, you can create your layouts easily and quickly with XML and get all benefits of using xml/resources meanwhile get the performance of creating views programmatically without even knowing about it! (I'm not joking , you don't need to learn anything new :))
 
**XmlByPass** supports almost 99% of tags and attributes of XML layouts including `<include>` and `<fragment>`, And for other 1%, it will auto generate an style resource and applies the style to the views. That's how you can be sure it will 100% work.

No need to worry about ViewBinding, **XmlByPass** adds views that have an `android:id` with their ID name as Public variables and other views are protected so still you can customize them by inheritance. (You will see sample codes in Usage)

**XmlByPass** supports all [qualifiers](https://developer.android.com/guide/topics/resources/providing-resources). Yay!

And one more interesting option, **XmlByPass** brings auto generating `ViewModel` with `LiveData` :)

## Usage

Let's start with Android Studio Hello World project!

This is `activity_main` (xml layout): 

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">
    
    <TextView
        android:id="@+id/tv"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello World!"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintBottom_toBottomOf="parent" />
    
</androidx.constraintlayout.widget.ConstraintLayout>
```

This is the MainActivity:
```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}
```
