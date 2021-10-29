import * as React from 'react';
import Button from '@material-ui/core/Button';
import TextField from '@material-ui/core/TextField';
import DeleteIcon from '@mui/icons-material/Delete';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import DialogTitle from '@material-ui/core/DialogTitle';

import {Grid, Paper, Typography } from '@material-ui/core';


export default function FormDialog({message, title}: {message:string, title: string}) {
  const [open, setOpen] = React.useState(false);
  const [emailAddress, setEmailAddress] = React.useState("");
  const [password, setPassword] = React.useState("");
  const [destEmailAddress, setDestEmailAddress] = React.useState("");
  const [destEmails, setDestEmails] = React.useState<string []> ([]);
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
            id="name"
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
              label="Destination Email Address"
              type="email"
              fullWidth
              variant="standard"
              value={destEmailAddress}
              onChange={(e) => setDestEmailAddress(e.target.value)}
            />
            </Grid>
            <Grid item lg={4}>
              <Button style={{color:"red",  width:"100%", margin:"20px 0px 0px 0px"}} onClick={(e) => addEmail(e, destEmailAddress)}>
                Add Email
              </Button>
            </Grid>
          </Grid>
          <DeletableList data={destEmails} setData={setDestEmails}/>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Cancel</Button>
          <Button onClick={handleClose}>Submit</Button>
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