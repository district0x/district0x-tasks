pragma solidity ^0.4.24;

import "./Ownable.sol";

/*
    Only Owner of contract can add and update Tasks.
    Everybody can add Bids to Tasks.
    Everybody can Vote once on Bid.
*/

contract DistrictTasks is Ownable {
    struct Bid {
        address creator;
        address[] voters;
        mapping(address => bool) voted;
    }

    struct Task {
        uint biddingEndsOn;
        bool isActive;
        Bid[] bids;
    }

    Task[] public tasks;


    event LogAddTask (
        uint indexed id,
        string title,
        bool isActive,
        uint biddingEndsOn
    );
    
    event LogUpdateTask(
        uint indexed id, 
        string title, 
        uint biddingEndsOn,
        bool isActive
    );

    event LogAddBid (
        uint indexed taskId,
        uint indexed bidId,
        string title,
        string url,
        string description,
        uint amount,
        address indexed creator
    );

    event LogRemoveBid(
        uint taskId,
        uint bidId
    );

    event LogAddVoter (
        uint indexed taskId,
        uint indexed bidId,
        address voter
    );


    modifier notEmptyString(string s) {
        require(bytes(s).length > 0, "String can't be empty");
        _;
    }

    modifier validBiddingEndsOn(uint biddingEndsOn) {
        require(biddingEndsOn > block.timestamp, "biddingEndsOn > block.timestamp failed, timestamp expired");
        _;
    }

    modifier activeTask(uint taskId) {
        require(tasks[taskId].isActive == true, "Task is set as not active by Onwer of the contract");
        _;
    }

    modifier voterDidntVote(uint taskId, uint bidId) {
        require(tasks[taskId].bids[bidId].voted[msg.sender] != true, "voter already voted");
        _;
    }

    /// tasks

    function addTask(string _title, uint _biddingEndsOn, bool _isActive)
    public
    onlyOwner
    notEmptyString(_title)
    validBiddingEndsOn(_biddingEndsOn)
    {
        uint _taskId = tasks.length++;
        tasks[_taskId].biddingEndsOn = _biddingEndsOn;
        tasks[_taskId].isActive = _isActive;
        emit LogAddTask(
            _taskId,
            _title,
            _isActive,
            _biddingEndsOn
        );
    }

    function updateTask(uint _taskId, string _title, uint _biddingEndsOn, bool _isActive)
    public
    onlyOwner
    notEmptyString(_title)
    validBiddingEndsOn(_biddingEndsOn)
    {
        tasks[_taskId].isActive = _isActive;
        tasks[_taskId].biddingEndsOn = _biddingEndsOn;
        emit LogUpdateTask(
            _taskId,
            _title,
            _biddingEndsOn,
            _isActive)
        ;
    }

    function countTasks() public view returns (uint){
        return tasks.length;
    }

    /// bids

    // amount is decimal with 2 points, but Solidity doesn't have float
    // (* amount 100) before add and (/ amount 100) after get from event
    function addBid(uint _taskId, string _title, string _url, string _description, uint _amount)
    public
    activeTask(_taskId)
    validBiddingEndsOn(tasks[_taskId].biddingEndsOn)
    notEmptyString(_title)
    notEmptyString(_description)
    {
        uint _bidId = tasks[_taskId].bids.length++;
        tasks[_taskId].bids[_bidId].creator = msg.sender;
        emit LogAddBid(_taskId, _bidId, _title, _url, _description, _amount, msg.sender);
    }

    // We can't change indexes of the bids, because it will mess with events
    function removeBid(uint _taskId, uint _bidId)
    public
    onlyOwner {
        delete tasks[_taskId].bids[_bidId];
        emit LogRemoveBid(_taskId, _bidId);
    }

    function countBids(uint _taskId) public view returns (uint){
        return tasks[_taskId].bids.length;
    }

    function getBid(uint _taskId, uint _bidId) public view returns (address) {
        return tasks[_taskId].bids[_bidId].creator;
    }

    /// voters

    function addVoter(uint _taskId, uint _bidId)
    public
    activeTask(_taskId)
    validBiddingEndsOn(tasks[_taskId].biddingEndsOn)
    voterDidntVote(_taskId, _bidId)
    {
        tasks[_taskId].bids[_bidId].voters.push(msg.sender);
        tasks[_taskId].bids[_bidId].voted[msg.sender] = true;
        emit LogAddVoter(_taskId, _bidId, msg.sender);
    }

    function getVoters(uint _taskId, uint _bidId) public view returns (address[]) {
        return tasks[_taskId].bids[_bidId].voters;
    }

    function countVoters(uint _taskId, uint _bidId) public view returns (uint){
        return tasks[_taskId].bids[_bidId].voters.length;
    }

    function isVoted(uint _taskId, uint _bidId, address _voter) public view returns (bool) {
        return (tasks[_taskId].bids[_bidId].voted[_voter] == true);
    }
}
