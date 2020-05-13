package io.vaan.letmespeak


import io.vaan.letmespeak.dto.Verb

import scala.collection.mutable.ListBuffer
import io.vaan.letmespeak.util.ResourceUtils.fetchVerbs
import scala.io.StdIn.readLine
import scala.util.{Failure, Random, Success, Try}

object LetMeSpeakConsole {
  def main(args: Array[String]): Unit = {
    val filename = args(0)
    val allVerbs = fetchVerbs(filename)

    val howManyTrain = Try (args(1).toInt) match {
      case Failure(_) => allVerbs.size
      case Success(value) => value
    }

    val verbs = Random.shuffle(
      allVerbs.slice(0, howManyTrain)
    )

    println(s"Start training $howManyTrain verbs from file $filename")

    val (countRightAnswer, countAllAnswers, needToLearn) = mainCycle(verbs)

    println
    println(s"$countRightAnswer/$countAllAnswers")

    if (needToLearn nonEmpty) {
      println("Need to learn this verbs:")
      needToLearn.foreach(x => println(s"${x.word}\t${x.v1}\t${x.v2}\t${x.v3}"))
    }
  }

  private def mainCycle(verbs: Seq[Verb]): (Int, Int, ListBuffer[Verb]) = {
    val needToLearn = new ListBuffer[Verb]()
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
      } else {
        println(s"Nope, correct answer is: ${verb.v1} ${verb.v2} ${verb.v3}")
        needToLearn.addOne(verb)
      }
    })

    (countRightAnswer, countAllAnswers, needToLearn)
  }
}
