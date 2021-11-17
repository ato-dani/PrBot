import * as React from 'react';
import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import DeleteIcon from '@mui/icons-material/Delete';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';

import {APIParameters, APIPath} from '../../config/ApiConfig';
import {Grid, Paper, Tooltip, Typography } from '@material-ui/core';
import {submitPost} from '../../actions/User';
import { useSnackbar } from 'notistack';


export default function FormDialog({message, title}: {message:string, title: string}) {
  const [open, setOpen] = React.useState(false);
  const [smtpServer, setSmtpServer] = React.useState("");
  const [smtpPort, setSmtpPort] = React.useState("");
  const [emailAddress, setEmailAddress] = React.useState("");
  const [password, setPassword] = React.useState("");
  const [destEmailAddress, setDestEmailAddress] = React.useState("");
  const [destEmails, setDestEmails] = React.useState<string []> ([]);
  const {enqueueSnackbar} = useSnackbar();
  const handleClickOpen = () => {
    setOpen(true);
  };
  const handleClose = () => {
    setOpen(false);
  };
  const addEmail = (e: React.MouseEvent, destEmailAddress: string) => {
    const newDestEmails:string[] = [...destEmails, destEmailAddress];
    setDestEmails(newDestEmails);
    setDestEmailAddress("");
  }
  const handleSubmit = async () => {
    const query = {
      [APIParameters.EMAIL_USERNAME]: emailAddress,
      [APIParameters.EMAIL_PASSWORD]: password,
      [APIParameters.TITLE]: title,
      [APIParameters.MESSAGE]: message,
      [APIParameters.SMTP_SERVER]: smtpServer,
      [APIParameters.SMTP_PORT]: smtpPort,
      [APIParameters.DEST_EMAILS]: destEmails.toString(),
    }
    await submitPost({url: APIPath.SUBMIT_EMAIL, query, enqueueSnackbar,});
  }
  
  return (
    <div>
      <Button variant="contained" onClick={handleClickOpen}>
        Email
      </Button>
      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>Email</DialogTitle>
        <DialogContent>
          <DialogContentText>
            To send your message with Email please sign-in to your Email account.
          </DialogContentText>
          <TextField
            autoFocus
            margin="dense"
            id="smtp"
            inputProps={{"data-testid": "smtpServer"}}
            label="SMTP server"
            type="text"
            fullWidth
            variant="standard"
            value={smtpServer}
            onChange={(e) => setSmtpServer(e.target.value)}
          />
          <TextField
            autoFocus
            margin="dense"
            id="port"
            inputProps={{"data-testid": "smtpPort"}}
            label="Port"
            type="text"
            fullWidth
            variant="standard"
            value={smtpPort}
            onChange={(e) => setSmtpPort(e.target.value)}
          />
          <TextField
            autoFocus
            margin="dense"
            id="name"
            inputProps={{"data-testid": "emailAddress"}}
            label="Email Address"
            type="email"
            fullWidth
            variant="standard"
            value={emailAddress}
            onChange={(e) => setEmailAddress(e.target.value)}
          />
            <TextField
            autoFocus
            margin="dense"
            id="name"
            inputProps={{"data-testid": "password"}}
            label="Password"
            type="password"
            fullWidth
            variant="standard"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <Grid container>
            <Grid item lg={8}>
            <TextField
              autoFocus
              margin="dense"
              id="name"
              inputProps={{"data-testid": "destEmailAddress"}}
              label="Destination Email Address"
              type="email"
              fullWidth
              variant="standard"
              value={destEmailAddress}
              onChange={(e) => setDestEmailAddress(e.target.value)}
            />
            </Grid>
            <Grid item lg={4}>
              <Tooltip title="Please enter a valid destination email address">
                <div>
                <Button style={{color:"red",  width:"100%", margin:"20px 0px 0px 0px"}} disabled={destEmailAddress.length === 0} onClick={(e) => addEmail(e, destEmailAddress)} data-testid="addEmail">
                  Add Email
                </Button>
                </div>
              </Tooltip>
            </Grid>
          </Grid>
          <DeletableList data={destEmails} setData={setDestEmails}/>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancel</Button>
          <Tooltip title="Title, text, SMTP host, SMTP port, email address, password and at least one dest email are required">
            <div>
            <Button disabled={(title.length === 0 || message.length === 0 || emailAddress.length === 0 || password.length === 0 || smtpPort.length === 0 || smtpServer.length === 0 || destEmails.length === 0)}onClick={handleSubmit} data-testid={"submit"}>Submit</Button>
            </div>
          </Tooltip>
        </DialogActions>
      </Dialog>
    </div>
  );
}
function DeletableList({data, setData}: {'data': string[], setData: React.Dispatch<React.SetStateAction<string[]>>}){
  const handleDelete = (idx: number) => {
    const dataCopy:string[] = [...data];
    dataCopy.splice(idx, 1);
    setData(dataCopy);
  }
    return (
      data.length > 0 ?( 
        <Paper>
          <Grid container style={{height:"40px", overflowY:"scroll", whiteSpace:"nowrap"}}>
            {data?.map((curData:string,idx: number) => {
              return (
                <Grid item style={{marginRight: "10px"}}>
                  <Grid container>
                      <Grid item>
                        <Typography variant="body1">
                          {curData}
                        </Typography>
                      </Grid>
                    <Grid item>
                      <div onClick={() => handleDelete(idx)} style={{cursor: "pointer"}}>
                        <DeleteIcon style={{color: 'red'}}></DeleteIcon>
                      </div>
                    </Grid>   
                  
                  </Grid> 
                </Grid>   
              );
            })}
          </Grid>
        </Paper>): (<> </>)
    );
}