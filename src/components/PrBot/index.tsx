import * as React from 'react';
import CssBaseline from '@material-ui/core/CssBaseline';
import TextField from '@material-ui/core/TextField';
import Box from '@material-ui/core/Box';
import Typography from '@material-ui/core/Typography';
import Container from '@material-ui/core/Container';
import { createTheme, ThemeProvider } from '@material-ui/core/styles';
import TwitterDialog from '../TwitterDialog';
import RedditDialog from '../RedditDialog';
import EmailDialog from '../EmailDialog';
import DiscordDialog from '../DiscordDialog';

import { Button, Tooltip } from '@material-ui/core';
import {APIPath, APIParameters} from '../../config/ApiConfig';
import { useSnackbar } from 'notistack';
import {submitPost} from '../../actions/User';

const theme = createTheme();

export default function GetMessage() {
  const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    console.log({
      message: data.get('message')
    });
  };
  const [message, setMessage] = React.useState("");
  const [title, setTitle] = React.useState("");
  const [redditAccessToken, setRedditAccessToken] = React.useState("");
  const [twitterAccessToken, setTwitterAccessToken] = React.useState("");
  const [twitterAccessTokenSecret, setTwitterAccessTokenSecret] = React.useState("");
  const [discordAccessToken, setDiscordAccessToken] = React.useState("");
  const [discordAccessTokenSecret, setDiscordAccessTokenSecret] = React.useState("");
  const { enqueueSnackbar} = useSnackbar();
  const submitAll = async() => {
    const queries = [
      {
        [APIParameters.ACCESS_TOKEN]: twitterAccessToken,
        [APIParameters.ACCESS_TOKEN_SECRET]: twitterAccessTokenSecret,
        [APIParameters.TITLE]: title,
        [APIParameters.MESSAGE]: message,},
        {
          [APIParameters.ACCESS_TOKEN]: redditAccessToken,
          [APIParameters.TITLE]: title,
          [APIParameters.MESSAGE]: message,},
          {
            [APIParameters.ACCESS_TOKEN]: discordAccessToken,
            [APIParameters.ACCESS_TOKEN_SECRET]: discordAccessTokenSecret,
            [APIParameters.TITLE]: title,
            [APIParameters.MESSAGE]: message,}
    ];
    const paths = [APIPath.SUBMIT_TWITTER, APIPath.SUBMIT_REDDIT, APIPath.SUBMIT_DISCORD];
    const requests = paths.map((path, idx) => {
      return submitPost({url: path, query:queries[idx], enqueueSnackbar,});
    })
    await Promise.all(requests)
  }
  return (
    <ThemeProvider theme={theme}>
      <Container component="main" maxWidth="md">
        <CssBaseline />
        <Box bgcolor="#c5cae9" 
          sx={{
            marginTop: 8,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            width: "md"
          }}
        >
          <Typography component="h1" variant="h5">
            PR-Bot
          </Typography>
          <Box component="form" onSubmit={handleSubmit}>
            <Tooltip title = "Title only used by Discord and Email">
              <TextField
                id="outlined-multiline-static"
                label="Title"
                multiline
                rows={1}
                defaultValue="Enter your title here"
                autoFocus
                fullWidth
                value={title}
                onChange={(e) => {if(e.target.value.length <= 300) setTitle(e.target.value)}}
              />
            </Tooltip>
          
            <p>{title.length} / 300</p>
            <TextField
              id="outlined-multiline-static"
              label="Message"
              multiline
              rows={4}
              defaultValue="Enter your message here"
              autoFocus
              fullWidth 
              value={message}
              onChange={(e) => {if(e.target.value.length <= 280) setMessage(e.target.value)}}
            />
            <p> {message.length} / 280</p>
            <Box
              sx={{
                marginTop: 8,
                display: 'flex',
                flexDirection: 'row',
                flexWrap:'nowrap',
                alignItems: 'center',
                justifyContent: "space-evenly"
              }}
            >
            <TwitterDialog message={message} title={title} accessToken={twitterAccessToken} accessTokenSecret={twitterAccessTokenSecret} setAccessToken={setTwitterAccessToken} setAccessTokenSecret={setTwitterAccessTokenSecret} />
            <DiscordDialog message={message} title={title} accessToken={discordAccessToken} accessTokenSecret={discordAccessTokenSecret} setAccessToken={setDiscordAccessToken} setAccessTokenSecret={setDiscordAccessTokenSecret}/>
            <RedditDialog message={message} title={title} accessToken={redditAccessToken} setAccessToken={setRedditAccessToken}/>
            <EmailDialog message={message} title={title}/>
            <Tooltip title="Sign in to reddit, twitter, discord and enter title & text to submit to all">
              <div>
                <Button variant="contained" onClick={submitAll} disabled={redditAccessToken.length === 0 || discordAccessToken.length === 0 || twitterAccessToken.length === 0 || title.length === 0 || message.length === 0 }>
                All
              </Button>
              </div>
            
            </Tooltip>
             
            </Box>
          </Box>
        </Box>
      </Container>
    </ThemeProvider>
  );
}