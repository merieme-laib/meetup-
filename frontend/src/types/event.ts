export interface Event {
  id: number
  title: string
  description: string
  date: string
  location: string
  city: string
  isOnline: boolean
  imageUrl: string
  price: number
  maxParticipants?: number
  category: string
  creatorId: number
  participantsCount: number
  likesCount: number
  isRegistered?: boolean
  isLiked?: boolean
}
