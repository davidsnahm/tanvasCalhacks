from scipy.misc import imread
from matplotlib import pyplot
from SRM import SRM

im = imread("fishscale.jpg")

srm = SRM(im, 256)
segmented = srm.run()

fig = pyplot.figure(frameon=False)
ax = pyplot.Axes(fig, [0., 0., 1., 1.])
ax.set_axis_off()
fig.add_axes(ax)

pyplot.imshow(segmented/256)
pyplot.show()
