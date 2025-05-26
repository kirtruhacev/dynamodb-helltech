# Hell-Tech 2025 Single table design - DynamoDB Demo

This project serves as a demo to showcasing single-table design and DynamoDB presented at Hell-Tech 2025.

## Project Overview

This project demonstrates how to manage multiple entity types (`User`, `Publication`, `Institution`) within a single DynamoDB table using flexible schema design. It leverages the AWS SDK v2 enhanced client for efficient database operations.

## Features

- **Single-Table Design**: Store multiple types of entities in a single table.
- **AWS SDK v2 Enhanced Client**: Use the enhanced client for streamlined DynamoDB interactions.
- **Composite Key Strategy**: Implement composite keys to differentiate between entity types.
- **Global Secondary Indexes (GSIs)**: Utilize GSIs for additional query capabilities based on secondary attributes.

## Getting Started

### Prerequisites

- **Java 21 or higher**: Ensure you have Java installed.
- **Gradle**: Build and manage the project dependencies.
- **AWS CLI**: For configuring AWS credentials (optional if running locally).
- **AWS DynamoDB Local**: Recommended for local development and testing.

### Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/helltech/dynamodb-helltech.git
