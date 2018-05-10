package closest_points_pair

import scala.util.Random

case class Point(x: Int, y: Int)

case class PointsPair(point1: Point, point2: Point) {
  def distance: Double = ClosestPoints.distance(point1, point2)
}

object ClosestPoints {
  def main(args: Array[String]): Unit = {
    val points = generate()
    println("Length: ", points.length)
    val bruteForcePair: PointsPair = bruteForceClosest(points)
    println("Brute Force closest: ", bruteForcePair)
  }

  def generate(length: Int = 10000, max: Int = 100000, seed: Int = 42): Vector[Point] = {
    val rnd = new Random(seed = seed)
    (0 until length).map(_ => Point(rnd.nextInt(max), rnd.nextInt(max))).toVector.distinct
  }

  def bruteForceClosest(points: Vector[Point]): PointsPair = {
    var minPair = PointsPair(points(0), points(1))

    val length = points.length
    for (i <- 0 until length - 1) {
      for (j <- i + 1 until length) {
        if (distance(points(i), points(j)) < minPair.distance) {
          minPair = PointsPair(points(i), points(j))
        }
      }
    }
    minPair
  }

  def divideAndConquerClosest(points: Vector[Point]): Double = {
    val (resultPoints, distance) = closestPair(points)
    distance
  }

  def closestPair(points: Vector[Point]): (Vector[Point], Double) = {
    if (points.length < 2)
      (points, Double.MaxValue)
    else {
      val (left, right, splitPoint) = splitByY(points)
      val (leftPoints, leftDistance) = closestPair(left)
      val (rightPoints, rightDistance) = closestPair(right)
      val pointsMerged = leftPoints ++ rightPoints
      val dist = boundaryMerge(leftPoints ++ rightPoints, leftDistance, rightDistance, splitPoint)
      (pointsMerged, dist)
    }
  }

  def splitByY(points: Vector[Point]): (Vector[Point], Vector[Point], Point) = {
    val sorted = sortByY(points)
    val lengthMedian = sorted.length / 2
    val splitPoint = sorted(lengthMedian)
    val (left, right) = sorted.splitAt(lengthMedian)
    (left, right, splitPoint)
  }

  def boundaryMerge(points: Vector[Point], leftDistance: Double, rightDistance: Double, middlePoint: Point): Double = {
    val lrMinDistance = Math.min(leftDistance, rightDistance)
    val xl = middlePoint.x - lrMinDistance
    val xr = middlePoint.x + lrMinDistance

    val minPoints = points.filter(p => p.x >= xl && p.x <= xr)
    val minPDistance = if (minPoints.length < 2) Double.MaxValue else
      (minPoints.slice(0, minPoints.length - 1), minPoints.slice(1, minPoints.length)).zipped.map((a, b) => distance(a, b)).min
    scala.math.min(lrMinDistance, minPDistance)
  }

  def sortByY(points: Vector[Point]): Vector[Point] = points.sortBy(point => point.y)

  def distance(point1: Point, point2: Point): Double = {
    math.sqrt(scala.math.pow(point1.x - point2.x, 2) + scala.math.pow(point1.y - point2.y, 2))
  }
}
