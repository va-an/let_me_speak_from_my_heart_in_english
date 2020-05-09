package io.vaan.letmespeak


import scala.io.Source
import scala.io.StdIn.readLine
import scala.util.{Failure, Random, Success, Try}

object LetMeSpeak {
  private case class Verb(
    word: String,
    v1: String,
    v2: String,
    v3: String
  )

  private def fetchVerbs(fileForOpen: String): Seq[Verb] = {
    val verbs = Source
      .fromResource(fileForOpen)
      .getLines
      .toSeq
      .map { x =>
        val split = x.split("\\|")

        Verb(
          word = split(3),
          v1 = split(0),
          v2 = split(1),
          v3 = split(2))
      }

    Random.shuffle(verbs)
  }

  private val fileForOpen = "verbs-top-50.dsv"

  def main(args: Array[String]): Unit = {
    val verbs = fetchVerbs(fileForOpen)

    var countRightAnswer = 0
    var countAllAnswers = 0

    verbs.foreach(verb => {
      countAllAnswers += 1

      print(s"${verb.word} >> ")
      val answer = readLine

      val isCorrect = Try {
        val splitAnswer = answer.split(" ")
        val isV1Right = verb.v1.split("/").contains(splitAnswer(0))
        val isV2Right = verb.v2.split("/").contains(splitAnswer(1))
        val isV3Right = verb.v3.split("/").contains(splitAnswer(2))

        isV1Right && isV2Right && isV3Right
      } match {
        case Failure(_) => false
        case Success(result) => result
      }

      if (isCorrect) {
        countRightAnswer += 1
        println(s"Yep, $countRightAnswer correct answers from $countAllAnswers")
      } else println(s"Nope, correct answer is: ${verb.v1} ${verb.v2} ${verb.v3}")
    })

    println(s"$countRightAnswer right answers from $countAllAnswers.")
  }
}
