from pygame import *

img = image.load(input('Target> '))
size = int(input('Tile Size> '))

for x in range(0, img.get_width(), size):
	for y in range(0, img.get_height(), size):
		image.save(img.subsurface(Rect(x, y, size, size)), str(x // size) + "_" + str(y // size) + ".png")

image.save(transform.scale(img, (200, 200)), "minimap.png")

