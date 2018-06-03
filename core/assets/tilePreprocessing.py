from pygame import *

x = int(input("Width: "))
y = int(input("Height: "))
tilesize = int(input("Tile Size: "))
filename = input("File: ")

newimg = Surface((x * tilesize + (x-1) * 2, y * tilesize + (y-1) * 2), SRCALPHA)

oldimg = image.load(filename)

cx = 0
cy = 0

for i in range(0, y):
    for j in range(0, x):
        newimg.blit(oldimg.subsurface(j * tilesize, i * tilesize, tilesize, tilesize), (cx, cy))
        cx += 2 + tilesize
    print(cx, cy)
    cy += 2 + tilesize
    cx = 0

image.save(newimg, "n"+filename)
