package it.sciencespir.smartbudget.persistence.service

import scalaz.concurrent.Task

trait CRUDService[M] {
  def initialize(): Task[Unit]
  def initializeIfNeeded: Task[Unit]

  def list(): Task[Seq[M]]
  def create(model: M): Task[M]
  def find(id: Int): Task[Option[M]]
  def update(model: M): Task[M]
  def delete(model: M): Task[_]
}
