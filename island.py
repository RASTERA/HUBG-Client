import random
import queue
from pygame import *
import time

w = int(input('Width: '))
h = int(input('Height: '))

map = [['w' for __ in range(h)] for _ in range(w)]

meh = set()

def gen():

	q = queue.Queue()
	q.put((random.randint(0, w), (random.randint(0, h))))

	pos = [(1, 0), (0, 1), (-1, 0), (0, -1)]

	def dist(x1, y1, x2, y2):
		return ((x1 - x2) ** 2 + (y1 - y2) ** 2) ** 0.5

	while not q.empty():

		x, y = q.get()

		if x >= w or x < 0 or y >= h or y < 0 or map[x][y] != 'w':
			continue

		if random.randint(0, 100 + max(dist(w // 2, h // 2, w, h) // 2, 5) // 4) == 0:
			map[x][y] = 't'
		else:
			map[x][y] = 'g'

		for p in pos:
			if random.randint(0, 1) == 0 and 0 <= x + p[0] < w and 0 <= y + p[1] < h and (x + p[0], y + p[1]) not in meh:
				q.put([x + p[0], y + p[1]])
				meh.add((x + p[0], y + p[1]))

while len(meh) < w * h * 0.8:
	print((w * h * 0.8) - len(meh))
	gen()

for fill in range(10, 3, -1):
	mask = []

	for x in range(fill):
		for y in range(fill):
			mask.append((x, y))

	for x in range(w):
		for y in range(h):
			land = 0

			try:

				if map[x][y] != 'g' or map[x][y + fill] != 'g' or map[x + fill][y] != 'g' or map[x + fill][y + fill] != 'g':
					break

				#print('<3')


				for m in mask:
					if map[x + m[0]][y + m[1]] == 'g':
						land += 1

				if land > (fill ** 2) // 2:
					for m in mask:
						map[x + m[0]][y + m[1]] = 'g'
			except:
				pass

"""
blk = 0.25

screen = display.set_mode((800, 800))

screen.fill((0, 0, 255))

for y in range(800):
	for x in range(800):
		tile = map[x][y]

		if tile == 'g':
			draw.rect(screen, (0, 255, 0), (x, y, max(1, blk), max(1, blk)))

		elif tile == 't':

			draw.rect(screen, (255, 255, 0), (x, y, max(1, blk), max(1, blk)))

display.flip()

input()
"""

with open('out.rah', 'w') as file:
	for x in range(w):
		file.write(' '.join(map[x]) + '\n')
