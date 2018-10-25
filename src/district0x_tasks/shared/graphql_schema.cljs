(ns district0x-tasks.shared.graphql-schema)

(def graphql-schema "
  scalar Date
  scalar Keyword

  type Query {
    activeTasks: [Task],
    bids(task_id: ID!): [Bid]
  }

  type Task {
    task_id: ID,
    task_title: String,
    task_isActive: Boolean,
    task_biddingEndsOn: Date,
    task_createdAt: Date,
    task_bids: [Bid]
  }

  type Bid {
    bid_id: ID,
    task_id: ID,
    bid_creator: String,
    bid_title: String,
    bid_url: String,
    bid_description: String,
    bid_amount: Float,
    bid_votesSum: Int,
    bid_createdAt: Date
  }
")
