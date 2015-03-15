# ImageCraft

This mod can load an image into the Minecraft world, and you also get 216 colored blocks under `ImageCraft` creative tab.
This is beta version.

## Contents

- [Installation](#installation)
- [Usage](#usage)
- [Build](#build)
- [Usage](#usage)
- [Screenshot](#screenshot)

## Installation

1. Install [Minecraft Forge](http://files.minecraftforge.net/) 1.8
2. Download the [ImageCraft-1.8-1.0.jar](./ImageCraft-1.8-1.0.jar?raw=true) file, and copy it to `.minecraft/mods`

## Usage

To build an image you can use the `/image` command.

Required arguments(you have to specify in the correct order):

| Name             | Value      | Description                                                                |
|------------------|------------|----------------------------------------------------------------------------|
| path             | path       | The path to the image. You can use JPEG, PNG, BMP, and GIF image format.   |
| width            | a number   | The width attribute specifies the width of the image, in blocks.           |
| height           | a number   | The height attribute specifies the height of the image, in blocks.         |

Example: `/image C:/some/folder/example.png 20 20`

Optional arguments(the order does not matter and you have to specify the name of the argument, too):

| Name         | Value                    | Description                                                        |
| -------------|--------------------------|--------------------------------------------------------------------|
| left         | no value                 | Build the image from the specified position to the left. This is the default behavior.  |
| right        | no value                 | Build the image from the specified position to the right. |
| up           | no value                 | Build the image upward. This is the default behavior. |
| forward      | no value                 | Build the image forward. |
| pos          | three numbers            | The position of the loaded image. If you don't specify the pos argument, then the pos will equal to the block in front of you.  |
| clear        | no value                 | Clear(set the blocks to air) the image instead of building it.      |
| scale        | smooth, nearest, bicubic | Image scaling type. It depends on the picture which one is better. The default value is nearest. |
| distance     | cie76, cie94, ciede2000  | Color distance options. It depends on the picture which one is better. The default value is cie94.  |
| alpha        | a number                 | If the number is 0 or negative then you turn off the transparency. If the number bigger than 255 then the whole picture will be transparent(equal to clear argument). The default value is 10.  |

Example: `/image C:/some/folder/example.png 20 30 left up pos -12 67 -34 alpha 20 scale smooth distance cie76`

## Build

Use gradlew.

```
./gradlew setupDecompWorkspace --refresh-dependencies
./gradlew eclipse
```

## Screenshot
Video:
<a href="https://www.youtube.com/watch?v=hOr_JgSSZo8" target="_blank"><img src="screenshot/youtube.png"></a>
---------------------------
![Example 1](screenshot/rainbow2.png?raw=true)
![Example 2](screenshot/nyan_cat.png?raw=true)
![Example 3](screenshot/spongyabob.png?raw=true)
![Example 4](screenshot/meme.png?raw=true)
![Example 5](screenshot/rainbow.png?raw=true)
