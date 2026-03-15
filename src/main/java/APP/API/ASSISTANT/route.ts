import { type NextRequest, NextResponse } from "next/server"

type AssistantResponse = {
  statusCode: number
  intent: string
  toolUsed?: string
  message: string
  data?: unknown
}

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

function unsupported(): NextResponse<AssistantResponse> {
  return NextResponse.json(
    {
      statusCode: 400,
      intent: "unsupported",
      message:
        "Unsupported query. Try: top 5 players by runs, find rankings of Virat Kohli, average of Virat Kohli from India, or find player Joe Root.",
    },
    { status: 400 },
  )
}

export async function POST(request: NextRequest) {
  const { query } = (await request.json()) as { query?: string }

  if (!query || !query.trim()) {
    return NextResponse.json(
      { statusCode: 400, intent: "unsupported", message: "Query cannot be empty" },
      { status: 400 },
    )
  }

  const normalized = query.trim().toLowerCase()

  const topMatch = normalized.match(/top\s+(\d+)\s+.*by\s+(runs|average|avg)/i)
  if (topMatch) {
    const limit = Number(topMatch[1])
    const metric = topMatch[2] === "avg" ? "average" : topMatch[2]

    const sorted = [...playerDatabase].sort((a, b) => {
      if (metric === "runs") return b.stats.runs - a.stats.runs
      return b.stats.average - a.stats.average
    })

    return NextResponse.json(
      {
        statusCode: 200,
        intent: "top_rankings",
        toolUsed: "getTopRankings",
        message: "Top rankings fetched successfully",
        data: {
          metric,
          limit,
          results: sorted.slice(0, limit),
        },
      },
      { status: 200 },
    )
  }



  const rankingMatch = query.match(/(?:find|get|show)?\s*(?:the\s+)?rankings?\s+(?:of|for)\s+([a-z .'-]+?)(?:\s+by\s+(runs|average|avg))?$/i)
  if (rankingMatch) {
    const playerName = rankingMatch[1].trim().toLowerCase()
    const metric = rankingMatch[2]?.toLowerCase() === "avg" ? "average" : rankingMatch[2]?.toLowerCase() || "runs"

    const sorted = [...playerDatabase].sort((a, b) => {
      if (metric === "runs") return b.stats.runs - a.stats.runs
      return b.stats.average - a.stats.average
    })

    const rankedResults = sorted.map((item, index) => ({
      ranking: index + 1,
      metric,
      metricValue: metric === "runs" ? item.stats.runs : item.stats.average,
      playerName: item.name,
      country: item.country,
    }))

    const found = rankedResults.find((item) => item.playerName.toLowerCase() === playerName)

    if (!found) {
      return NextResponse.json(
        { statusCode: 404, intent: "not_found", message: "Player not found" },
        { status: 404 },
      )
    }

    return NextResponse.json(
      {
        statusCode: 200,
        intent: "player_rankings",
        toolUsed: "getPlayerRankingHistory",
        message: "Player ranking fetched successfully",
        data: found,
      },
      { status: 200 },
    )
  }

  const avgMatch = query.match(/(?:average|avg)\s+(?:of|for)?\s*([a-z .'-]+?)(?:\s+from\s+([a-z .'-]+))?$/i)
  if (avgMatch) {
    const playerName = avgMatch[1].trim().toLowerCase()
    const country = avgMatch[2]?.trim().toLowerCase()

    const player = playerDatabase.find(
      (item) => item.name.toLowerCase() === playerName && (!country || item.country.toLowerCase() === country),
    )

    if (!player) {
      return NextResponse.json(
        { statusCode: 404, intent: "not_found", message: "Player not found" },
        { status: 404 },
      )
    }

    return NextResponse.json(
      {
        statusCode: 200,
        intent: "player_average",
        toolUsed: "findPlayer",
        message: "Player average calculated successfully",
        data: {
          playerName: player.name,
          country: player.country,
          average: player.stats.average,
          runs: player.stats.runs,
        },
      },
      { status: 200 },
    )
  }

  const findMatch = query.match(/(?:find|get|show)\s+player\s+([a-z .'-]+?)(?:\s+from\s+([a-z .'-]+))?$/i)
  if (findMatch) {
    const playerName = findMatch[1].trim().toLowerCase()
    const country = findMatch[2]?.trim().toLowerCase()

    const player = playerDatabase.find(
      (item) => item.name.toLowerCase() === playerName && (!country || item.country.toLowerCase() === country),
    )

    if (!player) {
      return NextResponse.json(
        { statusCode: 404, intent: "not_found", message: "Player not found" },
        { status: 404 },
      )
    }

    return NextResponse.json(
      {
        statusCode: 200,
        intent: "player_lookup",
        toolUsed: "findPlayer",
        message: "Player found",
        data: player,
      },
      { status: 200 },
    )
  }

  return unsupported()
}
