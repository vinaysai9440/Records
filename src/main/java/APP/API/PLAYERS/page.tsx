"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Label } from "@/components/ui/label"

type PlayerData = {
  name: string
  country: string
  stats: {
    matches: number
    runs: number
    hundreds: number
    fifties: number
    average: number
  }
}

type AssistantResponse = {
  statusCode: number
  intent: string
  toolUsed?: string
  message: string
  data?: unknown
}

export default function PlayerSearch() {
  const [playerName, setPlayerName] = useState("")
  const [country, setCountry] = useState("")
  const [playerData, setPlayerData] = useState<PlayerData | null>(null)
  const [error, setError] = useState("")
  const [loading, setLoading] = useState(false)

  const [assistantQuery, setAssistantQuery] = useState("")
  const [assistantData, setAssistantData] = useState<AssistantResponse | null>(null)
  const [assistantError, setAssistantError] = useState("")
  const [assistantLoading, setAssistantLoading] = useState(false)

  const searchPlayer = async () => {
    if (!playerName.trim()) {
      setError("Player name is required")
      return
    }

    setLoading(true)
    setError("")

    try {
      const params = new URLSearchParams()
      params.append("playerName", playerName)
      if (country) params.append("country", country)

      const response = await fetch(`/api/players?${params.toString()}`)

      if (!response.ok) {
        if (response.status === 404) {
          setError("Player not found")
        } else {
          setError("An error occurred while fetching player data")
        }
        setPlayerData(null)
        return
      }

      const data = (await response.json()) as PlayerData
      setPlayerData(data)
    } catch (err) {
      setError("Failed to fetch player data")
      setPlayerData(null)
    } finally {
      setLoading(false)
    }
  }

  const askAssistant = async () => {
    if (!assistantQuery.trim()) {
      setAssistantError("Please enter a question")
      return
    }

    setAssistantLoading(true)
    setAssistantError("")

    try {
      const response = await fetch("/api/assistant", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ query: assistantQuery }),
      })

      const data = (await response.json()) as AssistantResponse

      if (!response.ok) {
        setAssistantError(data.message || "Assistant could not process the query")
        setAssistantData(null)
        return
      }

      setAssistantData(data)
    } catch (err) {
      setAssistantError("Failed to reach assistant")
      setAssistantData(null)
    } finally {
      setAssistantLoading(false)
    }
  }

  return (
    <div className="container mx-auto py-10 max-w-3xl space-y-6">
      <Card>
        <CardHeader>
          <CardTitle className="text-2xl">Player Statistics Search</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="playerName">Player Name (required)</Label>
              <Input
                id="playerName"
                value={playerName}
                onChange={(e) => setPlayerName(e.target.value)}
                placeholder="Enter player name"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="country">Country (optional)</Label>
              <Input
                id="country"
                value={country}
                onChange={(e) => setCountry(e.target.value)}
                placeholder="Enter country"
              />
            </div>

            <Button onClick={searchPlayer} disabled={loading} className="w-full">
              {loading ? "Searching..." : "Search Player"}
            </Button>

            {error && <div className="text-red-500 text-sm mt-2">{error}</div>}

            {playerData && (
              <div className="mt-4 p-4 border rounded-md bg-muted">
                <h3 className="font-bold text-lg">{playerData.name}</h3>
                <p className="text-muted-foreground">{playerData.country}</p>
                <div className="mt-2 grid grid-cols-2 gap-2">
                  <div>Matches: {playerData.stats.matches}</div>
                  <div>Runs: {playerData.stats.runs}</div>
                  <div>Hundreds: {playerData.stats.hundreds}</div>
                  <div>Fifties: {playerData.stats.fifties}</div>
                  <div>Average: {playerData.stats.average}</div>
                </div>
              </div>
            )}
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle className="text-2xl">Stats Assistant (Natural Language)</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="assistantQuery">Ask a question</Label>
              <Input
                id="assistantQuery"
                value={assistantQuery}
                onChange={(e) => setAssistantQuery(e.target.value)}
                placeholder="e.g. top 5 batters by average in test"
              />
              <p className="text-xs text-muted-foreground">
                Try: "top 5 players by runs", "find rankings of Virat Kohli", "average of Virat Kohli from India", "find player Joe Root".
              </p>
            </div>

            <Button onClick={askAssistant} disabled={assistantLoading} className="w-full">
              {assistantLoading ? "Thinking..." : "Ask Assistant"}
            </Button>

            {assistantError && <div className="text-red-500 text-sm mt-2">{assistantError}</div>}

            {assistantData && (
              <div className="mt-4 p-4 border rounded-md bg-muted space-y-2">
                <div><span className="font-semibold">Intent:</span> {assistantData.intent}</div>
                {assistantData.toolUsed && <div><span className="font-semibold">Tool:</span> {assistantData.toolUsed}</div>}
                <div><span className="font-semibold">Message:</span> {assistantData.message}</div>
                <pre className="text-xs overflow-auto p-2 rounded bg-background border">
                  {JSON.stringify(assistantData.data, null, 2)}
                </pre>
              </div>
            )}
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
