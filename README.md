# Virtual Art Gallery - Java AWT

A virtual gallery application for showcasing digital art built with Java AWT.

## Features

- **Art Display**: View digital artworks in a centered canvas with a dark background
- **Navigation**: Browse through multiple artworks using Previous/Next buttons or arrow keys
- **Zoom Functionality**: Zoom in and out to see artwork details
- **Information Panel**: Displays title, artist, and description for each artwork
- **Keyboard Controls**: 
  - Left/Right arrow keys: Navigate between artworks
  - Plus/Minus keys: Zoom in/out
- **Sample Artworks**: 5 procedurally generated digital art pieces included

## How to Run

### Compile:
```bash
javac VirtualGallery.java Artwork.java
```

### Run:
```bash
java VirtualGallery
```

## Adding Your Own Images

To add your own images to the gallery, modify the `initializeArtworks()` method in `VirtualGallery.java`:

```java
// Load image from file
BufferedImage myImage = ImageIO.read(new File("path/to/your/image.jpg"));
artworks.add(new Artwork("Your Title", "Your Name", "Description", myImage));
```

## Controls

- **Previous Button / Left Arrow**: View previous artwork
- **Next Button / Right Arrow**: View next artwork
- **Zoom In (+)**: Increase artwork size
- **Zoom Out (-)**: Decrease artwork size
- **Close Window**: Exit the application

## Technical Details

- Built with Java AWT (Abstract Window Toolkit)
- No external dependencies required
- Compatible with Java 8 and above
- Generates sample abstract art procedurally using Graphics2D
- Supports common image formats (JPG, PNG, GIF) when loading from files

## Project Structure

- `VirtualGallery.java`: Main application with UI and navigation logic
- `Artwork.java`: Data model for artwork objects
- `README.md`: Documentation

## Customization

You can customize:
- Window size in the constructor: `setSize(1000, 700)`
- Background colors for different panels
- Zoom limits (currently 0.4x to 3.0x)
- Add more artworks or load from a directory
- Modify the sample artwork generation algorithms

Enjoy your virtual art gallery!
