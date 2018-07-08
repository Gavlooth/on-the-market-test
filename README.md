# on-the-market-test


FIXME: description
This is the "graphical editor" developed as part of the
on the market interview

## Installation
*git clone  https://github.com/Gavlooth/on-the-market-test.git
*lein uberjar.



## Usage

    $ java -jar graphical-editor-0.1.0-standalone.jar [args]


Commands
*The editor supports 7 commands:
*I M N​. Create a new M x N image with all pixels coloured white (O).
*C​. Clears the table, setting all pixels to white (O).
*L X Y C​. Colours the pixel (X,Y) with colour C.
*V X Y1 Y2 C​. Draw a vertical segment of colour C in column X between rows Y1 and Y2
*(inclusive).
*H X1 X2 Y C​. Draw a horizontal segment of colour C in row Y between columns X1 and X2
*(inclusive).
*F X Y C​. Fill the region R with the colour C. R is defined as: Pixel (X,Y) belongs to R. Any other
*pixel which is the same colour as (X,Y) and shares a common side with any pixel in R also
*belongs to this region.
*S​. Show the contents of the current image
*X​. Terminate the session

### Bugs
No known bags
...

## License

Copyright © 2018 Christos Chatzifountas

Distributed under the Eclipse Public License
