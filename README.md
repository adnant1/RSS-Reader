# RSS Reader

## Overview

This project is a program designed to parse RSS 2.0 XML feeds and generate a well-formatted HTML page. The HTML page includes dynamic content derived from the RSS feed, including the channel title, description, and a table of news items with their publication dates, sources, and titles.

## Features

- **Read RSS 2.0 Feed**: Accepts a URL to an RSS 2.0 feed and processes the XML data.
- **Generate HTML Page**: Creates an HTML file with:
  - **Page Title**: Set to the `<channel>` title or "Empty Title" if not available.
  - **Header**: Displays the channel title as a link to the channel's URL.
  - **Description**: Shows the channel's description or "No description" if missing.
  - **News Table**: Includes rows for each news item with:
    - **Publication Date**: Shows the publication date or "No date available".
    - **Source**: Provides a clickable link to the source URL or "No source available".
    - **Title/Description**: Displays the title or description of the news item, with a link if available, or "No title available".
