import { type NextRequest, NextResponse } from "next/server"

// This would typically come from a database
const playerDatabase = [
{
name: "Virat Kohli",
country: "India",
stats: {
matches: 102,
runs: 8074,
hundreds: 27,
fifties: 28,
average: 50.15,
},
},
{
name: "Joe Root",
country: "England",
stats: {
matches: 124,
runs: 10629,
hundreds: 29,
fifties: 56,
average: 49.2,
},
},
{
name: "Kane Williamson",
country: "New Zealand",
stats: {
matches: 92,
runs: 7683,
hundreds: 24,
fifties: 33,
average: 52.62,
},
},
]

export async function GET(request: NextRequest) {
  // Get search parameters from the URL
  const searchParams = request.nextUrl.searchParams
  const playerName = searchParams.get("playerName")
  const country = searchParams.get("country")

  // Check if playerName is provided
  if (!playerName) {
    return NextResponse.json({ error: "Player name is required" }, { status: 400 })
  }

  // Search for player in database
  let foundPlayers = playerDatabase.filter((player) => player.name.toLowerCase().includes(playerName.toLowerCase()))

  // If country is provided, filter by country as well
  if (country && foundPlayers.length > 0) {
    foundPlayers = foundPlayers.filter((player) => player.country.toLowerCase() === country.toLowerCase())
  }

  // Return player stats or 404 if not found
  if (foundPlayers.length > 0) {
    return NextResponse.json(foundPlayers[0])
  } else {
    return NextResponse.json({ error: "Player not found" }, { status: 404 })
  }
}

