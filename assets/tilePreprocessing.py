from pygame import *

x = int(input("Width: "))
y = int(input("Height: "))
tilesize = int(input("Tile Size: "))
filename = input("File: ")

newimg = Surface((x * tilesize + x * 2, y * tilesize + y * 2), SRCALPHA)

oldimg = image.load(filename)

cx = 0
cy = 0

for i in range(0, y):
    for j in range(0, x):
        tile = oldimg.subsurface(j * tilesize, i * tilesize, tilesize, tilesize)
        newimg.set_at((cx,cy), tile.get_at((0, 0)))
        newimg.set_at((cx+tilesize+1,cy), tile.get_at((tilesize-1, 0)))
        newimg.set_at((cx,cy+tilesize+1), tile.get_at((0, tilesize-1)))
        newimg.set_at((cx+tilesize+1, cy+tilesize+1), tile.get_at((tilesize-1, tilesize-1)))
        
        for k in range(0, tilesize):
            newimg.set_at((cx+1+k,cy), tile.get_at((k, 0)))
            newimg.set_at((cx+1+k,cy+tilesize+1), tile.get_at((k, tilesize-1)))

            newimg.set_at((cx,cy+k+1), tile.get_at((0, k)))
            newimg.set_at((cx+tilesize+1,cy+k+1), tile.get_at((tilesize-1, k)))

        newimg.blit(tile, (cx+1, cy+1))
            
        cx += 2 + tilesize
        
    print(cx, cy)
    cy += 2 + tilesize
    cx = 0

image.save(newimg, "n"+filename)
