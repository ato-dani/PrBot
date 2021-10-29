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
          <TextField
              id="outlined-multiline-static"
              label="Title"
              multiline
              rows={1}
              defaultValue="Enter your title here"
              autoFocus
              fullWidth
              value={title}
              onChange={(e) => setTitle(e.target.value)}
            />
            <TextField
              id="outlined-multiline-static"
              label="Message"
              multiline
              rows={4}
              defaultValue="Enter your message here"
              autoFocus
              fullWidth 
              value={message}
              onChange={(e) => setMessage(e.target.value)}
            />
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
            <TwitterDialog message={message} title={title}/>
            <DiscordDialog message={message} title={title}/>
            <RedditDialog message={message} title={title}/>
            <EmailDialog message={message} title={title}/>
            </Box>
          </Box>
        </Box>
      </Container>
    </ThemeProvider>
  );
}