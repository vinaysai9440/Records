"use client"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Label } from "@/components/ui/label"

export default function PlayerSearch() {
  const [playerName, setPlayerName] = useState("")
  const [country, setCountry] = useState("")
  const [playerData, setPlayerData] = useState(null)
  const [error, setError] = useState("")
  const [loading, setLoading] = useState(false)

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

      const data = await response.json()
      setPlayerData(data)
    } catch (err) {
      setError("Failed to fetch player data")
      setPlayerData(null)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="container mx-auto py-10 max-w-md">
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
    </div>
  )
}

