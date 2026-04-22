import type { Event } from '@/types/event'

export type EventWithOnlineFlag = Event & { online?: boolean }

export function parseEventDate(date: string | null | undefined) {
  if (!date) return null

  const parsedDate = new Date(date)
  return Number.isNaN(parsedDate.getTime()) ? null : parsedDate
}

export function getCurrentYearEventDate(date: string | null | undefined) {
  const parsedDate = parseEventDate(date)
  if (!parsedDate) return null

  const now = new Date()
  const normalizedDate = new Date(parsedDate)
  normalizedDate.setFullYear(now.getFullYear())
  return normalizedDate
}

export function isPastEventDate(date: string | null | undefined) {
  const parsedDate = getCurrentYearEventDate(date)
  if (!parsedDate) return false

  return parsedDate.getTime() < Date.now()
}

export function isUpcomingEventDate(date: string | null | undefined) {
  return !isPastEventDate(date)
}

export function isEventOnline(event: EventWithOnlineFlag) {
  return Boolean(event.isOnline || event.online)
}
