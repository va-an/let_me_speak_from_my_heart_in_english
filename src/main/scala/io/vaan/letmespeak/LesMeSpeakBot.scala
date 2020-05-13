package io.vaan.letmespeak

import canoe.api._
import canoe.models.Chat
import canoe.syntax._
import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.functor._
import fs2.Stream
import io.vaan.letmespeak.dto.Verb
import io.vaan.letmespeak.util.ResourceUtils.fetchVerbs

object LesMeSpeakBot extends IOApp {
  private val verbs = fetchVerbs("256.dsv")

  override def run(args: List[String]): IO[ExitCode] = {
    val token: String = args.head

    Stream
      .resource(TelegramClient.global[IO](token))
      .flatMap { implicit client =>
        Bot.polling[IO].follow(
          start(service),
          stop,
          help
        )
      }
      .compile
      .drain
      .as(ExitCode.Success)
  }

  def start[F[_]: TelegramClient](service: Service[F]): Scenario[F, Unit] =
    for {
      chat <- Scenario.expect(command("start").chat)
      _ <- play(chat, service).stopOn(command("stop").isDefinedAt)
    } yield ()

  def stop[F[_]: TelegramClient]: Scenario[F, Unit] =
    for {
      msg <- Scenario.expect(command("stop"))
      _   <- Scenario.eval(msg.chat.send("stopped!"))
    } yield ()

  def help[F[_]: TelegramClient]: Scenario[F, Unit] =
    for {
      msg <- Scenario.expect(command("help"))
      _   <- Scenario.eval(msg.chat.send("help text"))
    } yield ()

  def play[F[_]: TelegramClient](chat: Chat, service: Service[F]): Scenario[F, Unit] =
    for {
      _ <- Scenario.eval(chat.send("Сколько глаголов будем учить?"))
      howManyLearn <- Scenario.expect(text)
      _ <- Scenario.eval(chat.send(s"howManyLearn = $howManyLearn"))
      _ <- Scenario.eval(service.process(verbs))
    } yield ()

  trait Service[F[_]] {
    def process(verbs: List[Verb]): F[Unit]
  }

  val service: Service[IO] = new Service[IO] {
    override def process(verbs: List[Verb]): IO[Unit] = {
      for {
        _ <- IO(println(System.currentTimeMillis()))
      } yield ()
    }
  }
}
